package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.data.network.NetworkMonitor
import lt.vitalijus.cmp_custom_pagination.data.persistence.OrderRepository
import lt.vitalijus.cmp_custom_pagination.data.repository.FavoritesRepository
import lt.vitalijus.cmp_custom_pagination.data.repository.ProductsRepository
import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.OrderStatus
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductRating
import lt.vitalijus.cmp_custom_pagination.domain.paging.PagingEvent
import lt.vitalijus.cmp_custom_pagination.domain.paging.ProductPagingFactory
import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

/**
 * Redux-style Store for managing Products state:
 * - handles intents, mutations, and emits effects.
 * - includes state machine validation for intents.
 */
class ProductsStore(
    pagerFactory: ProductPagingFactory,
    private val addToBasketUseCase: AddToBasketUseCase,
    private val stateMachine: ProductsStateMachine,
    private val orderRepository: OrderRepository,
    private val favoritesRepository: FavoritesRepository,
    private val productsRepository: ProductsRepository,
    private val settingsRepository: lt.vitalijus.cmp_custom_pagination.data.persistence.SettingsRepository,
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    private val _effects = Channel<ProductsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val _intentFlow = MutableSharedFlow<ProductsIntent>(
        extraBufferCapacity = 64,
        replay = 0
    )
    val intentFlow = _intentFlow.asSharedFlow()

    init {
        // Load persisted data including layout preference and last sync timestamp
        scope.launch {
            loadPersistedData()

            // Load settings
            val settings = settingsRepository.getSettings()
            
            // Load last sync timestamp
            val lastSyncTimestamp = settingsRepository.getLastSyncTimestamp()
            if (lastSyncTimestamp != null) {
                dispatchMutation(ProductsMutation.SyncTimestampUpdated(lastSyncTimestamp))
            }
            
            // Load saved layout preference
            dispatchMutation(ProductsMutation.ViewLayoutModeChanged(settings.viewLayoutPreference))
            
            // Load show sync timestamp preference
            dispatchMutation(ProductsMutation.ShowSyncTimestampChanged(settings.showSyncTimestamp))
            
            // Load allItemsLoaded flag (remembers if all products were fetched)
            val allItemsLoaded = settingsRepository.getAllItemsLoaded()
            if (allItemsLoaded) {
                dispatchMutation(ProductsMutation.AllItemsLoaded)
                println("📋 Restored allItemsLoaded = true from settings")
            }
        }
        
        // Start automatic refresh based on sync frequency setting
        scope.launch {
            startAutoRefreshMonitor()
        }
        
        // Monitor network connectivity in separate coroutine
        scope.launch {
            networkMonitor.isConnected.collect { isConnected ->
                dispatchMutation(ProductsMutation.NetworkStatusChanged(isConnected))
            }
        }

        // Load cached products ONCE on startup (offline-first)
        scope.launch {
            productsRepository.getCachedProducts()
                .onSuccess { cachedProducts ->
                    if (cachedProducts.isNotEmpty()) {
                        // Display cached products instantly
                        dispatchMutation(ProductsMutation.ProductsLoaded(products = cachedProducts))
                    }
                }
        }

        // Check if cache needs refresh in background
        scope.launch {
            val shouldRefresh = productsRepository.shouldRefresh()
            if (shouldRefresh) {
                // Cache is old or doesn't exist - trigger pager to load
                pager.loadNextProducts()
            }
        }

        // Collect intents and process them through the state machine
        scope.launch {
            intentFlow.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    private suspend fun loadPersistedData() {
        val orders = orderRepository.getOrders()
        val basket = orderRepository.getBasket()
        val favorites = orderRepository.getFavorites()
        val deliveryAddress = orderRepository.getLastDeliveryAddress()
        val paymentMethod = orderRepository.getLastPaymentMethod()

        println("🔄 DEBUG: Loading persisted data - basket size: ${basket.size}")

        _state.update {
            it.copy(
                orders = orders,
                basketItems = basket,
                favoriteProductIds = favorites,
                currentDeliveryAddress = deliveryAddress,
                currentPaymentMethod = paymentMethod
            )
        }
    }

    // Track current page number for cache management
    private var currentPageNumber = 0
    
    private val pager: ProductPager = pagerFactory.create { event ->
        when (event) {
            is PagingEvent.LoadingChanged -> {
                dispatchMutation(
                    mutation = ProductsMutation.SetLoading(isLoading = event.isLoading)
                )
            }

            is PagingEvent.ProductsLoaded -> {
                val isRefreshingFirstPage = _state.value.isRefreshing && currentPageNumber == 0
                
                // If we're refreshing AND this is the first page (page 0), clear old data first
                if (isRefreshingFirstPage) {
                    println("🧹 Clearing old products (refresh successful, showing new data)")
                    _state.update { currentState ->
                        currentState.copy(
                            products = emptyList(),
                            productCache = emptyMap()
                        )
                    }
                }

                // Now load the new products (they'll be appended via reducer)
                dispatchMutation(mutation = ProductsMutation.ProductsLoaded(products = event.products))

                // Save loaded products to cache for offline access
                scope.launch {
                    productsRepository.cacheProducts(event.products, page = currentPageNumber)
                    currentPageNumber++ // Increment for next page
                    println("📦 Cached ${event.products.size} products for page ${currentPageNumber - 1}")
                }
                
                // ✅ Update sync timestamp ONLY when refresh completes successfully with first page
                if (isRefreshingFirstPage && event.products.isNotEmpty()) {
                    val timestamp = lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis()
                    scope.launch {
                        settingsRepository.saveLastSyncTimestamp(timestamp)
                        dispatchMutation(ProductsMutation.SyncTimestampUpdated(timestamp))
                        println("⏰ Sync timestamp updated to $timestamp (after successful refresh)")
                    }
                }
            }

            is PagingEvent.Error -> {
                // Only show error if we have no data (not in offline mode with cached data)
                val hasData = _state.value.products.isNotEmpty()
                
                if (hasData) {
                    // We have cached data - this is offline mode, not a real error
                    println("ℹ️ Network unavailable, working in offline mode with ${_state.value.products.size} cached items")
                    // Don't dispatch error mutation or show error effect
                    // Just mark loading as complete
                    dispatchMutation(ProductsMutation.SetLoading(isLoading = false))
                } else {
                    // No data available - this is a real error
                    dispatchMutation(
                        mutation = ProductsMutation.LoadingError(
                            message = event.message ?: "Unknown error"
                        )
                    )
                    emitEffect(
                        effect = ProductsEffect.ShowError(
                            message = event.message ?: "Unknown error"
                        )
                    )
                }
            }
        }
    }

    /**
     * Main entry point for user intents.
     * Emits to SharedFlow for state machine processing.
     */
    fun processIntent(intent: ProductsIntent) {
        _intentFlow.tryEmit(intent)
    }

    /**
     * Handles intents with state machine validation.
     * The state machine validates transitions and blocks invalid ones.
     * Invalid transitions during pagination are expected (fast scrolling) and handled silently.
     */
    private fun handleIntent(intent: ProductsIntent) {
        try {
            // Validate transition
            println("🔍 DEBUG: Processing intent: $intent")
            stateMachine.transition(intent)

            // Execute the intent
            when (intent) {
                ProductsIntent.LoadMore -> handleLoadMore()
                ProductsIntent.LoadAllItems -> handleLoadAllItems()
                ProductsIntent.ManualRefresh -> handleManualRefresh()
                is ProductsIntent.LoadFavorites -> handleLoadFavorites(intent.favoriteIds)
                is ProductsIntent.SearchProducts -> handleSearchProducts(intent.query)
                is ProductsIntent.SetSortOption -> handleSetSortOption(intent.sortOption)
                is ProductsIntent.SetViewLayoutMode -> handleSetViewLayoutMode(intent.layoutMode)
                is ProductsIntent.SetShowSyncTimestamp -> handleSetShowSyncTimestamp(intent.show)

                is ProductsIntent.AddToBasket -> handleAddToBasket(intent.product, intent.quantity)

                is ProductsIntent.UpdateQuantity -> handleUpdateQuantity(
                    intent.productId,
                    intent.newQuantity
                )

                is ProductsIntent.RemoveProduct -> handleRemoveProduct(intent.productId)

                ProductsIntent.ClearBasket -> handleClearBasket()

                is ProductsIntent.NavigateTo -> handleNavigation(intent.screen)

                is ProductsIntent.ToggleFavorite -> handleToggleFavorite(intent.productId)

                is ProductsIntent.SetDeliveryAddress -> handleSetDeliveryAddress(intent.address)

                is ProductsIntent.SetPaymentMethod -> handleSetPaymentMethod(intent.paymentMethod)

                ProductsIntent.ConfirmOrder -> handleConfirmOrder()

                is ProductsIntent.UpdateOrderStatus -> handleUpdateOrderStatus(
                    intent.orderId,
                    intent.status
                )

                is ProductsIntent.RateProduct -> handleRateProduct(
                    intent.orderId,
                    intent.productId,
                    intent.rating,
                    intent.comment
                )
            }
        } catch (e: IllegalStateException) {
            // Invalid transition blocked by state machine
            // This is expected during fast scrolling/pagination - silently ignore
            println("⚠️ DEBUG: State machine blocked intent: $intent, error: ${e.message}")
        }
    }

    /**
     * Dispatch a mutation to update state via reducer.
     * Also updates the state machine based on mutation results.
     */
    private fun dispatchMutation(mutation: ProductsMutation) {
        _state.update { currentState ->
            val newState = ProductsReducer.reduce(
                state = currentState,
                mutation = mutation
            )
            scope.launch {
                persistState(newState, mutation)
            }
            newState
        }

        stateMachine.applyMutation(mutation = mutation)
    }

    private suspend fun persistState(state: ProductsState, mutation: ProductsMutation) {
        // Only persist relevant mutations to avoid excessive writes
        when (mutation) {
            is ProductsMutation.OrderCreated,
            is ProductsMutation.OrderUpdated -> {
                state.orders.forEach { order ->
                    orderRepository.saveOrder(order)
                }
            }

            is ProductsMutation.BasketUpdated -> {
                // Persist basket items whenever they change
                println("💾 DEBUG: Persisting basket items - size: ${state.basketItems.size}")
                orderRepository.saveBasket(state.basketItems)
            }

            is ProductsMutation.FavoriteToggled -> {
                orderRepository.saveFavorites(state.favoriteProductIds)
            }

            is ProductsMutation.DeliveryAddressSet -> {
                state.currentDeliveryAddress?.let {
                    orderRepository.saveDeliveryAddress(it)
                }
            }

            is ProductsMutation.PaymentMethodSet -> {
                state.currentPaymentMethod?.let {
                    orderRepository.savePaymentMethod(it)
                }
            }
            
            is ProductsMutation.AllItemsLoaded -> {
                // Persist that all items were loaded so we don't ask to load again on app restart
                settingsRepository.saveAllItemsLoaded(true)
                println("💾 Persisted allItemsLoaded = true")
            }

            else -> { /* No persistence needed for other mutations */
            }
        }
    }

    private fun emitEffect(effect: ProductsEffect) {
        scope.launch {
            _effects.send(effect)
        }
    }

    private fun handleLoadMore() {
        scope.launch {
            pager.loadNextProducts()
        }
    }
    
    /**
     * Monitor sync frequency setting and trigger automatic refreshes.
     * Checks every minute if refresh is needed based on user's sync frequency preference.
     */
    private suspend fun startAutoRefreshMonitor() {
        println("🔄 ========================================")
        println("🔄 Auto-refresh monitor STARTED")
        println("🔄 ========================================")
        
        while (true) {
            try {
                println("\n🔍 ========== AUTO-REFRESH CHECK ==========")
                
                // Get current settings
                val settings = settingsRepository.getSettings()
                val syncFrequency = settings.syncFrequency
                println("🔍 Sync frequency: ${syncFrequency.displayName} (${syncFrequency.durationMs}ms)")
                
                // Check if manual only
                if (syncFrequency == lt.vitalijus.cmp_custom_pagination.domain.model.SyncFrequency.MANUAL_ONLY) {
                    println("⏭️ AUTO-REFRESH SKIPPED: MANUAL_ONLY mode")
                    println("🔍 ==========================================\n")
                    delay(60.seconds)
                    continue
                }
                
                // Check if already refreshing
                val isCurrentlyRefreshing = _state.value.isRefreshing
                println("🔍 Currently refreshing: $isCurrentlyRefreshing")
                if (isCurrentlyRefreshing) {
                    println("⏭️ AUTO-REFRESH SKIPPED: Already refreshing")
                    println("🔍 ==========================================\n")
                    delay(60.seconds)
                    continue
                }
                
                // Get last sync timestamp
                val lastSyncTimestamp = settingsRepository.getLastSyncTimestamp()
                println("🔍 Last sync timestamp: $lastSyncTimestamp")
                
                if (lastSyncTimestamp != null) {
                    val now = lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis()
                    val timeSinceLastSync = now - lastSyncTimestamp
                    val minutesSinceSync = timeSinceLastSync / 60000
                    
                    println("🔍 Current time: $now")
                    println("🔍 Time since last sync: ${timeSinceLastSync}ms ($minutesSinceSync minutes)")
                    println("🔍 Required interval: ${syncFrequency.durationMs}ms")
                    
                    // Trigger refresh if interval has passed
                    if (timeSinceLastSync >= syncFrequency.durationMs) {
                        println("⏰ ✅ AUTO-REFRESH TRIGGERED!")
                        println("⏰ Reason: ${timeSinceLastSync}ms >= ${syncFrequency.durationMs}ms")
                        println("🔍 ==========================================\n")
                        handleManualRefresh()
                        // After refresh, wait full interval before next check
                        delay(syncFrequency.durationMs.coerceAtLeast(10_000)) // Min 10 seconds
                        continue
                    } else {
                        val remainingMs = syncFrequency.durationMs - timeSinceLastSync
                        val remainingMinutes = remainingMs / 60000
                        val remainingSeconds = (remainingMs % 60000) / 1000
                        println("⏱️ Next auto-refresh in ~$remainingMinutes min ${remainingSeconds}s")
                        println("🔍 ==========================================\n")
                        // ✅ Wait for the remaining time (smart wait)
                        println("💤 Waiting ${remainingMs}ms (${remainingMinutes}m ${remainingSeconds}s) until next refresh...")
                        delay(remainingMs.coerceAtLeast(10_000)) // Min 10 seconds, max remaining time
                        continue
                    }
                } else {
                    println("⏰ ✅ AUTO-REFRESH TRIGGERED!")
                    println("⏰ Reason: Initial sync (no lastSyncTimestamp)")
                    println("🔍 ==========================================\n")
                    handleManualRefresh()
                    // After initial sync, wait full interval
                    delay(syncFrequency.durationMs.coerceAtLeast(10_000))
                    continue
                }
                
            } catch (e: Exception) {
                println("❌ Auto-refresh monitor error: ${e.message}")
                e.printStackTrace()
                // On error, wait a bit before retrying
                delay(10.seconds)
            }
        }
    }

    private fun handleManualRefresh() {
        scope.launch {
            dispatchMutation(ProductsMutation.SetRefreshing(isRefreshing = true))
            
            try {
                println("🔄 Starting manual refresh...")
                
                // 1. Clear database cache (removes ALL stale data)
                // BUT keep UI data visible until new data arrives!
                productsRepository.clearCache()
                println("🗑️ Database cache cleared")
                
                // 2. Reset page counter (critical for proper caching)
                currentPageNumber = 0
                println("📄 Page counter reset to 0")
                
                // 3. Reset pager to initial state (offset/cursor = 0, isEndReached = false)
                pager.reset()
                println("↩️ Pager reset to initial state")
                
                // 4. Prepare state for refresh (but DON'T clear products yet - keep them visible!)
                _state.update { currentState ->
                    currentState.copy(
                        allItemsLoaded = false,  // Reset pagination flag
                        error = null             // Clear any errors
                        // products stays as-is → user sees old data while loading
                    )
                }
                // Reset persisted allItemsLoaded flag
                settingsRepository.saveAllItemsLoaded(false)
                println("🔄 State prepared for refresh (old data still visible, allItemsLoaded reset)")
                
                // 5. Load first page from API (will be cached automatically by pager event handler)
                // When products arrive, they'll replace old data via ProductsLoaded mutation
                pager.loadNextProducts()
                
                // 6. On SUCCESS: Clear old data and show new data
                // This happens automatically in PagingEvent.ProductsLoaded handler
                // We need to clear products there BEFORE appending new ones
                
                // 7. Sync timestamp is now updated in PagingEvent.ProductsLoaded handler
                // (after products actually arrive successfully)
                
                println("✅ Manual refresh initiated (waiting for products to load...)")
            } catch (e: Exception) {
                println("❌ Manual refresh failed: ${e.message}")
                dispatchMutation(ProductsMutation.LoadingError("Refresh failed: ${e.message}"))
            } finally {
                dispatchMutation(ProductsMutation.SetRefreshing(isRefreshing = false))
            }
        }
    }

    private fun handleLoadAllItems() {
        scope.launch {
            dispatchMutation(ProductsMutation.SetLoadingAllItems(isLoading = true))
            
            // If already loaded, just mark as complete and return
            if (_state.value.allItemsLoaded) {
                println("✓ All items already loaded (${_state.value.products.size} items)")
                dispatchMutation(ProductsMutation.SetLoadingAllItems(isLoading = false))
                return@launch
            }
            
            // Keep loading until all items are fetched
            // Note: Images are loaded but can be lazy-loaded in UI for better performance
            var consecutiveNoChanges = 0
            var hasError = false
            
            while (!_state.value.allItemsLoaded) {
                // Check if there are more items to load by attempting to load
                val currentSize = _state.value.products.size
                val errorBefore = _state.value.error
                
                pager.loadNextProducts()
                
                // Wait a bit for the load to complete
                kotlinx.coroutines.delay(300) // Reduced delay for faster loading
                
                // Check if we got an error (network unavailable in offline mode)
                val errorAfter = _state.value.error
                if (errorAfter != null && errorAfter != errorBefore && currentSize > 0) {
                    // We have an error but have cached data - offline mode
                    println("ℹ️ Working in offline mode with ${currentSize} cached items")
                    dispatchMutation(ProductsMutation.AllItemsLoaded)
                    hasError = true
                    break
                }
                
                // If size hasn't changed after loading, we've reached the end
                if (_state.value.products.size == currentSize) {
                    consecutiveNoChanges++
                    // Confirm no changes after 2 attempts to be sure
                    if (consecutiveNoChanges >= 2) {
                        println("✓ All items loaded (${_state.value.products.size} items total)")
                        dispatchMutation(ProductsMutation.AllItemsLoaded)
                        break
                    }
                } else {
                    consecutiveNoChanges = 0 // Reset counter if we got new items
                }
                
                // Safety check: if loading takes too long, break after reasonable limit
                if (_state.value.products.size > 500) {
                    println("⚠️ Loaded 500+ items, stopping to prevent memory issues")
                    dispatchMutation(ProductsMutation.AllItemsLoaded)
                    break
                }
            }
            
            dispatchMutation(ProductsMutation.SetLoadingAllItems(isLoading = false))
            
            // Show info message if we're in offline mode
            if (hasError && _state.value.products.isNotEmpty()) {
                emitEffect(
                    effect = ProductsEffect.ShowError(
                        message = "Working in offline mode with ${_state.value.products.size} cached items"
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleLoadFavorites(favoriteIds: Set<Long>) {
        scope.launch {
            if (favoriteIds.isEmpty()) {
                // No favorites to load
                dispatchMutation(ProductsMutation.FavoritesLoaded(products = emptyList()))
                return@launch
            }

            // Show loading indicator initially
            dispatchMutation(ProductsMutation.SetLoadingFavorites(isLoading = true))

            // Collect from reactive Flow - observe CURRENT favorite IDs from state!
            // Use flatMapLatest to cancel previous Flow when IDs change
            state
                .map { it.favoriteProductIds }
                .distinctUntilChanged()
                .flatMapLatest { currentFavoriteIds ->
                    if (currentFavoriteIds.isEmpty()) {
                        // No favorites anymore - emit empty flow
                        flowOf(emptyList())
                    } else {
                        // Observe favorites from database for current IDs
                        // This Flow will be CANCELLED when currentFavoriteIds change!
                        favoritesRepository.observeFavorites(ids = currentFavoriteIds)
                    }
                }
                .collect { products ->
                    // Update UI with latest data from database
                    dispatchMutation(ProductsMutation.FavoritesLoaded(products = products))
                    dispatchMutation(ProductsMutation.SetLoadingFavorites(isLoading = false))
                }

            // Check if we need to refresh from network in the background
            val shouldRefresh = favoritesRepository.shouldRefresh(favoriteIds)

            if (shouldRefresh) {
                try {
                    // Fetch fresh data from network and update cache
                    // The Flow above will automatically emit the new data!
                    favoritesRepository.refreshFavorites(ids = favoriteIds)
                        .onFailure { error ->
                            // Only show error if we don't have any cached data
                            favoritesRepository.getCachedFavorites(ids = favoriteIds)
                                .onSuccess { cached ->
                                    if (cached.isEmpty()) {
                                        emitEffect(
                                            effect = ProductsEffect.ShowError(
                                                message = error.message
                                                    ?: "Failed to load favorites"
                                            )
                                        )
                                    }
                                }
                        }
                } catch (e: Exception) {
                    emitEffect(
                        effect = ProductsEffect.ShowError(
                            message = e.message ?: "Failed to load favorites"
                        )
                    )
                }
            }
        }
    }

    private fun handleSearchProducts(query: String) {
        dispatchMutation(ProductsMutation.SearchQueryChanged(query))
    }

    private fun handleSetSortOption(sortOption: lt.vitalijus.cmp_custom_pagination.domain.model.SortOption) {
        dispatchMutation(ProductsMutation.SortOptionChanged(sortOption))
    }

    private fun handleSetViewLayoutMode(layoutMode: lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference) {
        dispatchMutation(ProductsMutation.ViewLayoutModeChanged(layoutMode))
        // Also persist to settings
        scope.launch {
            settingsRepository.saveViewLayoutPreference(layoutMode)
        }
    }
    
    private fun handleSetShowSyncTimestamp(show: Boolean) {
        dispatchMutation(ProductsMutation.ShowSyncTimestampChanged(show))
        // Also persist to settings
        scope.launch {
            settingsRepository.saveShowSyncTimestamp(show)
        }
    }

    private fun handleAddToBasket(
        product: Product,
        quantity: Int
    ) {
        println("🛒 DEBUG: handleAddToBasket called - product=${product.id} (${product.title}), quantity=$quantity")
        println("🛒 DEBUG: Current basket size: ${_state.value.basketItems.size}")
        
        val result = addToBasketUseCase.execute(
            currentItems = _state.value.basketItems,
            product = product,
            quantity = quantity
        )

        result.fold(
            onSuccess = { updatedItems ->
                println("🛒 DEBUG: AddToBasket SUCCESS - new basket size: ${updatedItems.size}")
                dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = updatedItems))
                emitEffect(effect = ProductsEffect.ShowBasketUpdated)
            },
            onFailure = { error ->
                println("❌ DEBUG: AddToBasket FAILED - error: ${error.message}")
                emitEffect(
                    effect = ProductsEffect.ShowError(
                        message = error.message ?: "Failed to add to basket"
                    )
                )
            }
        )
    }

    private fun handleUpdateQuantity(productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            handleRemoveProduct(productId = productId)
            return
        }

        val updatedItems = _state.value.basketItems.map { item ->
            if (item.product.id == productId) {
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }

        dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = updatedItems))
    }

    private fun handleRemoveProduct(productId: Long) {
        val updatedItems = _state.value.basketItems.filter { it.product.id != productId }
        dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = updatedItems))
    }

    private fun handleClearBasket() {
        dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = emptyList()))
    }

    private fun handleNavigation(screen: Screen) {
        emitEffect(effect = ProductsEffect.NavigateTo(screen))
    }

    private fun handleToggleFavorite(productId: Long) {
        println("🔍 DEBUG: handleToggleFavorite called with productId=$productId")
        val currentFavorites = _state.value.favoriteProductIds
        val isAdded = !currentFavorites.contains(productId)
        println("🔍 DEBUG: Current favorites: $currentFavorites, isAdded=$isAdded")

        dispatchMutation(mutation = ProductsMutation.FavoriteToggled(productId = productId))
        emitEffect(effect = ProductsEffect.ShowFavoriteToggled(isAdded = isAdded))
        println("🔍 DEBUG: Mutation dispatched and effect emitted")

        scope.launch {
            if (isAdded) {
                // Add to cache when favorited - insert the product immediately into database
                // Try to find product in products list or favoriteProductsData
                val product = _state.value.products.find { it.id == productId }
                    ?: _state.value.favoriteProductsData.find { it.id == productId }

                if (product != null) {
                    // Insert into database so the Flow will emit it immediately
                    favoritesRepository.addToCache(product)
                }
            } else {
                // Remove from cache if unfavorited - Flow will emit updated list
                favoritesRepository.removeFromCache(productId)
            }
        }
    }

    private fun handleSetDeliveryAddress(address: DeliveryAddress) {
        dispatchMutation(mutation = ProductsMutation.DeliveryAddressSet(address = address))
    }

    private fun handleSetPaymentMethod(paymentMethod: PaymentMethod) {
        dispatchMutation(mutation = ProductsMutation.PaymentMethodSet(paymentMethod = paymentMethod))
    }

    private fun handleConfirmOrder() {
        val currentState = _state.value
        val address = currentState.currentDeliveryAddress
        val payment = currentState.currentPaymentMethod
        val items = currentState.basketItems

        if (address == null || payment == null || items.isEmpty()) {
            emitEffect(effect = ProductsEffect.ShowError(message = "Missing order information"))
            return
        }

        val nowMillis = currentTimeMillis()
        val order = Order(
            id = "ORDER-$nowMillis",
            items = items,
            totalAmount = currentState.totalRetailPrice,
            status = OrderStatus.PAYMENT_PROCESSING,
            deliveryAddress = address,
            paymentMethod = payment,
            createdAt = nowMillis,
            estimatedDeliveryTime = nowMillis + (30 * 60 * 1000) // 30 minutes
        )

        dispatchMutation(mutation = ProductsMutation.OrderCreated(order = order))

        emitEffect(ProductsEffect.OrderCreated(order.id))

        // Start delivery simulation
        scope.launch {
            simulateOrderDelivery(order.id)
        }
    }

    private suspend fun simulateOrderDelivery(orderId: String) {
        val statuses = listOf(
            OrderStatus.PAYMENT_CONFIRMED to 2000L,
            OrderStatus.PREPARING to 5000L,
            OrderStatus.OUT_FOR_DELIVERY to 8000L,
            OrderStatus.DELIVERED to 10000L
        )

        for ((status, delay) in statuses) {
            kotlinx.coroutines.delay(delay)
            handleUpdateOrderStatus(orderId, status)
        }
    }

    private fun handleUpdateOrderStatus(orderId: String, status: OrderStatus) {
        val currentOrder = _state.value.orders.find { it.id == orderId } ?: return

        val updatedOrder = currentOrder.copy(
            status = status,
            actualDeliveryTime = if (status == OrderStatus.DELIVERED) {
                currentTimeMillis()
            } else {
                currentOrder.actualDeliveryTime
            }
        )

        dispatchMutation(mutation = ProductsMutation.OrderUpdated(order = updatedOrder))

        if (status == OrderStatus.DELIVERED) {
            emitEffect(effect = ProductsEffect.OrderDelivered(orderId = orderId))
        }
    }

    private fun handleRateProduct(orderId: String, productId: Long, rating: Int, comment: String) {
        val currentOrder = _state.value.orders.find { it.id == orderId } ?: return

        val productRating = ProductRating(
            productId = productId,
            rating = rating,
            comment = comment,
            createdAt = currentTimeMillis()
        )

        val updatedRatings = currentOrder.ratings + (productId to productRating)
        val updatedOrder = currentOrder.copy(ratings = updatedRatings)

        dispatchMutation(mutation = ProductsMutation.OrderUpdated(order = updatedOrder))
    }
}
