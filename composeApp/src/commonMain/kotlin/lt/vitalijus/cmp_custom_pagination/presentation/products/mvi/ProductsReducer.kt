package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

/**
 * Pure reducer function: (State, Mutation) -> State
 *
 * No side effects allowed here - must be pure and synchronous.
 * The mutation type itself tells us what state changes to make.
 */
object ProductsReducer {

    fun reduce(
        state: ProductsState,
        mutation: ProductsMutation
    ): ProductsState {
        return when (mutation) {
            is ProductsMutation.SetLoading -> {
                state.copy(
                    isLoadingMore = mutation.isLoading,
                    error = if (mutation.isLoading) null else state.error
                )
            }

            is ProductsMutation.SetLoadingAllItems -> {
                state.copy(
                    isLoadingAllItems = mutation.isLoading,
                    error = if (mutation.isLoading) null else state.error
                )
            }

            is ProductsMutation.SetRefreshing -> {
                state.copy(
                    isRefreshing = mutation.isRefreshing,
                    error = if (mutation.isRefreshing) null else state.error
                )
            }

            is ProductsMutation.AllItemsLoaded -> {
                state.copy(
                    allItemsLoaded = true,
                    isLoadingAllItems = false
                )
            }
            
            is ProductsMutation.SyncTimestampUpdated -> {
                state.copy(
                    lastSyncTimestamp = mutation.timestamp
                )
            }

            is ProductsMutation.ProductsLoaded -> {
                // Deduplicate products by ID (prevents duplicates from cache + pagination)
                val existingIds = state.products.map { it.id }.toSet()
                val newProducts = mutation.products.filter { it.id !in existingIds }

                // Add to product cache for instant detail view access
                val newCache = state.productCache + mutation.products.associateBy { it.id }

                state.copy(
                    products = state.products + newProducts,
                    productCache = newCache,
                    isLoadingMore = false,
                    error = null
                )
            }

            is ProductsMutation.LoadingError -> {
                state.copy(
                    isLoadingMore = false,
                    error = mutation.message
                )
            }

            is ProductsMutation.SearchQueryChanged -> {
                state.copy(searchQuery = mutation.query)
            }

            is ProductsMutation.SortOptionChanged -> {
                state.copy(sortOption = mutation.sortOption)
            }

            is ProductsMutation.ViewLayoutModeChanged -> {
                state.copy(viewLayoutMode = mutation.layoutMode)
            }
            
            is ProductsMutation.ShowSyncTimestampChanged -> {
                state.copy(showSyncTimestamp = mutation.show)
            }

            is ProductsMutation.NetworkStatusChanged -> {
                state.copy(isConnectedToInternet = mutation.isConnected)
            }

            is ProductsMutation.BasketUpdated -> {
                println("🛒 DEBUG: Reducer - BasketUpdated: before=${state.basketItems.size} items, after=${mutation.items.size} items")
                state.copy(
                    basketItems = mutation.items,
                    isLoadingMore = false,
                    error = null
                )
            }

            is ProductsMutation.FavoriteToggled -> {
                val updatedFavorites = if (mutation.productId in state.favoriteProductIds) {
                    state.favoriteProductIds - mutation.productId
                } else {
                    state.favoriteProductIds + mutation.productId
                }
                println("🔍 DEBUG: Reducer - FavoriteToggled: productId=${mutation.productId}, before=${state.favoriteProductIds}, after=$updatedFavorites")
                state.copy(
                    favoriteProductIds = updatedFavorites
                )
            }

            is ProductsMutation.SetLoadingFavorites -> {
                state.copy(
                    isLoadingFavorites = mutation.isLoading,
                    error = if (mutation.isLoading) null else state.error
                )
            }

            is ProductsMutation.FavoritesLoaded -> {
                // Add favorites to cache for instant access
                val newCache = state.productCache + mutation.products.associateBy { it.id }

                state.copy(
                    favoriteProductsData = mutation.products,
                    productCache = newCache,
                    isLoadingFavorites = false,
                    error = null
                )
            }

            is ProductsMutation.DeliveryAddressSet -> {
                state.copy(
                    currentDeliveryAddress = mutation.address
                )
            }

            is ProductsMutation.PaymentMethodSet -> {
                state.copy(
                    currentPaymentMethod = mutation.paymentMethod
                )
            }

            is ProductsMutation.OrderCreated -> {
                state.copy(
                    currentOrder = mutation.order,
                    orders = state.orders + mutation.order,
                    basketItems = emptyList(), // Clear basket after order creation
                    currentDeliveryAddress = null, // Clear delivery address for next order
                    currentPaymentMethod = null // Clear payment method for next order
                )
            }

            is ProductsMutation.OrderUpdated -> {
                val updatedOrders = state.orders.map { order ->
                    if (order.id == mutation.order.id) mutation.order else order
                }
                state.copy(
                    currentOrder = if (state.currentOrder?.id == mutation.order.id) {
                        mutation.order
                    } else {
                        state.currentOrder
                    },
                    orders = updatedOrders
                )
            }

            is ProductsMutation.ProductRated -> {
                state.copy(
                    currentOrder = state.currentOrder?.let { order ->
                        if (order.id == mutation.orderId) order else order
                    }
                )
            }
        }
    }
}
