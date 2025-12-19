@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lt.vitalijus.cmp_custom_pagination.di.AppKoinComponent
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsEffect
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.ScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes.FavoritesDetailScene
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes.ListDetailScene
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes.rememberDynamicSceneStrategy
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.basket.BasketScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.details.ProductDetailsScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.favorites.FavoritesScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.DeliveryScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.OrderRatingScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.OrderTrackingScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.OrdersHistoryScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.PaymentScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.products.ProductsScreen
import org.koin.core.component.inject

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.Products as NavKey,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys as Set<NavKey>,
    )

    // Get current backstack for the active top-level route
    val currentBackStack = navigationState.backStacks[navigationState.topLevelRoute]

    // Get the current route (last item in backstack)
    val currentRoute = currentBackStack?.last()

    // Or check the top-level route
    val topLevelRoute = navigationState.topLevelRoute

    val navigator = remember {
        Navigator(navigationState = navigationState)
    }

    val viewModel: ProductsViewModel by AppKoinComponent.inject()

    val screenTitleProvider: ScreenTitleProvider by AppKoinComponent.inject()
    val settingsRepository: lt.vitalijus.cmp_custom_pagination.data.persistence.SettingsRepository by AppKoinComponent.inject()

    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Load sync frequency for auto-refresh countdown display (reactive to setting changes)
    var syncFrequency by remember { mutableStateOf<lt.vitalijus.cmp_custom_pagination.domain.model.SyncFrequency?>(null) }
    
    // Reload sync frequency whenever we navigate (to pick up settings changes)
    LaunchedEffect(currentRoute) {
        val settings = settingsRepository.getSettings()
        syncFrequency = settings.syncFrequency
        println("🔍 NavigationRoot: Loaded syncFrequency = ${syncFrequency?.displayName} (${syncFrequency?.durationMs}ms)")
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Check if we're on a wide screen that would show list-detail scene
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWideScreen = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    // Determine the selected product ID for highlighting in the list
    val selectedProductId = if (isWideScreen && currentRoute is Route.ProductDetail) {
        currentRoute.productId
    } else {
        null
    }

    // Auto-navigate to first product on wide screens for list-detail scene
    LaunchedEffect(
        isWideScreen,
        topLevelRoute,
        state.products,
        state.favoriteProductIds,
        state.favoriteProductsData
    ) {
        when {
            // Auto-navigate to first product in Products list
            isWideScreen &&
                    topLevelRoute == Route.Products &&
                    currentRoute == Route.Products &&
                    state.products.isNotEmpty() -> {
                val firstProductId = state.products.first().id
                navigator.navigate(Route.ProductDetail(productId = firstProductId))
            }
            // Auto-navigate to first favorite product in Favorites list
            isWideScreen &&
                    topLevelRoute == Route.Favorites &&
                    currentRoute == Route.Favorites &&
                    state.favoriteProductIds.isNotEmpty() -> {
                // Ensure favorite products are loaded
                viewModel.processIntent(ProductsIntent.LoadFavorites(state.favoriteProductIds))

                // Wait for favoriteProductsData to be loaded, then navigate to first one
                if (state.favoriteProductsData.isNotEmpty()) {
                    val firstFavoriteProductId = state.favoriteProductsData.first().id
                    navigator.navigate(Route.ProductDetail(productId = firstFavoriteProductId))
                }
            }
        }
    }

    // Handle favorite removal when viewing detail in Favorites scene
    LaunchedEffect(isWideScreen, topLevelRoute, currentRoute, state.favoriteProductIds, state.favoriteProductsData) {
        // Only handle this when scene is active (wide screen + Favorites + viewing a detail)
        if (isWideScreen && 
            topLevelRoute == Route.Favorites && 
            currentRoute is Route.ProductDetail) {
            
            val currentProductId = currentRoute.productId
            
            // Check if the currently displayed product is no longer a favorite
            if (!state.favoriteProductIds.contains(currentProductId)) {
                // Product was removed from favorites, navigate to another favorite
                if (state.favoriteProductsData.isNotEmpty()) {
                    // Find a different favorite to show (prefer the next one, or the first one)
                    val nextFavorite = state.favoriteProductsData.firstOrNull { it.id != currentProductId }
                    nextFavorite?.let { product ->
                        navigator.navigate(Route.ProductDetail(productId = product.id))
                    }
                } else {
                    // No more favorites, go back to Favorites list
                    navigator.goBack()
                }
            }
        }
    }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProductsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is ProductsEffect.OrderCreated -> {
                    // Navigate to orders top-level destination
                    // Note: Basket is automatically cleared by the OrderCreated mutation in the reducer
                    // Clear the Basket's navigation stack (remove Delivery/Payment screens)
                    navigator.clearBackstack(Route.Basket)
                    navigator.navigate(Route.Orders)
                }

                is ProductsEffect.OrderDelivered -> {
                    snackbarHostState.showSnackbar("Your order has been delivered!")
                }

                else -> {
                    // Other effects can be handled as needed
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            val title = screenTitleProvider.getTitleForRoute(currentRoute)
            val backstackSize = currentBackStack?.size ?: 0

            // Determine if we should show back button
            // Only show back button for nested screens (not top-level destinations)
            // Hide back button when list-detail scene is active (wide screen + ProductDetail)
            val showBackButton = when (currentRoute) {
                Route.Products -> false
                Route.Favorites -> false
                Route.Basket -> false
                Route.Orders -> false
                is Route.ProductDetail -> {
                    // Don't show back button if scene is active (wide screen with Products or Favorites)
                    val isSceneActive = isWideScreen &&
                            (topLevelRoute == Route.Products || topLevelRoute == Route.Favorites)
                    !isSceneActive
                }

                null -> false
                else -> backstackSize > 1
            }

            // Get the previous route for "Back to X" text
            val previousRoute = when {
                // If we're at a nested screen (backstack > 1), get the previous screen in the stack
                backstackSize > 1 -> currentBackStack?.get(backstackSize - 2)
                // If we're at a top-level route that's not Products, go back to Products
                currentRoute != Route.Products && currentRoute == topLevelRoute -> Route.Products
                // Otherwise use the current top level route
                else -> topLevelRoute
            }

            val backToText = when {
                !showBackButton -> null
                else -> "Back to ${screenTitleProvider.getTitleForRoute(previousRoute)}"
            }

            TopAppBar(
                title = {
                    if (showBackButton && backToText != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { navigator.goBack() }
                            ) {
                                Icon(
                                    imageVector = AppIcons.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                            Text(
                                text = backToText,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC8E6C9) // Light green matching bottom nav bar
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedKey = navigationState.topLevelRoute,
                onSelectKey = { selectedKey ->
                    navigator.navigate(route = selectedKey)
                },
                basketItemCount = state.totalQuantity,
                favoritesItemCount = state.favoriteProductIds.size
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = {
                // Don't allow back navigation when scene is active and viewing ProductDetail
                val isSceneActive = isWideScreen &&
                        currentRoute is Route.ProductDetail &&
                        (topLevelRoute == Route.Products || topLevelRoute == Route.Favorites)
                if (!isSceneActive) {
                    navigator.goBack()
                }
            },
            sceneStrategy = rememberDynamicSceneStrategy(),
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.Products>(
                        metadata = ListDetailScene.listPane()
                    ) {
                        ProductsScreen(
                            state = state,
                            onIntent = viewModel::processIntent,
                            onProductClick = { productId ->
                                navigator.navigate(Route.ProductDetail(productId = productId))
                            },
                            onFavoriteClick = { productId ->
                                viewModel.processIntent(ProductsIntent.ToggleFavorite(productId))
                            },
                            selectedProductId = selectedProductId,
                            syncIntervalMs = syncFrequency?.durationMs
                        )
                    }
                    entry<Route.ProductDetail>(
                        metadata = when (topLevelRoute) {
                            Route.Products -> ListDetailScene.detailPane()
                            Route.Favorites -> FavoritesDetailScene.detailPane()
                            else -> emptyMap() // No scene metadata for Basket or other routes
                        }
                    ) { it ->
                        // Try to find product from any available source (Products, Favorites, or Basket)
                        val product = state.findProduct(it.productId) ?: return@entry

                        val isFavorite = state.favoriteProductIds.contains(it.productId)

                        ProductDetailsScreen(
                            product = product,
                            isFavorite = isFavorite,
                            onAddToBasket = { quantity ->
                                viewModel.processIntent(
                                    intent = ProductsIntent.AddToBasket(
                                        product = product,
                                        quantity = quantity
                                    )
                                )
                            },
                            onNavigateToBasket = {
                                navigator.navigate(route = Route.Basket)
                            },
                            onFavoriteClick = {
                                viewModel.processIntent(
                                    intent = ProductsIntent.ToggleFavorite(
                                        productId = product.id,
                                    )
                                )
                            }
                        )
                    }
                    entry<Route.Basket> {
                        BasketScreen(
                            state = state,
                            onIntent = viewModel::processIntent,
                            onProductClick = { productId ->
                                navigator.navigate(route = Route.ProductDetail(productId = productId))
                            },
                            onProceedToCheckout = {
                                navigator.navigate(route = Route.Delivery)
                            }
                        )
                    }
                    entry<Route.Favorites>(
                        metadata = FavoritesDetailScene.listPane()
                    ) {
                        FavoritesScreen(
                            state = state,
                            onProductClick = { productId ->
                                navigator.navigate(route = Route.ProductDetail(productId = productId))
                            },
                            onFavoriteClick = { productId ->
                                viewModel.processIntent(
                                    intent = ProductsIntent.ToggleFavorite(
                                        productId = productId,
                                    )
                                )
                            },
                            selectedProductId = selectedProductId,
                            onIntent = viewModel::processIntent
                        )
                    }
                    entry<Route.Orders> {
                        OrdersHistoryScreen(
                            orders = state.orders,
                            onOrderClick = { orderId ->
                                navigator.navigate(route = Route.OrderTracking(orderId = orderId))
                            }
                        )
                    }
                    entry<Route.Settings> {
                        val settingsRepository: lt.vitalijus.cmp_custom_pagination.data.persistence.SettingsRepository by AppKoinComponent.inject()
                        val settings by remember {
                            mutableStateOf(
                                runBlocking { settingsRepository.getSettings() }
                            )
                        }
                        var currentSettings by remember { mutableStateOf(settings) }
                        
                        lt.vitalijus.cmp_custom_pagination.presentation.settings.SettingsScreen(
                            settings = currentSettings,
                            onViewLayoutChange = { preference ->
                                // Dispatch intent to ViewModel for reactive UI update
                                viewModel.processIntent(ProductsIntent.SetViewLayoutMode(preference))
                                // Also update local state for immediate Settings screen update
                                currentSettings = currentSettings.copy(viewLayoutPreference = preference)
                            },
                            onNotificationsChange = { enabled ->
                                viewModel.viewModelScope.launch {
                                    settingsRepository.saveNotificationsEnabled(enabled)
                                    currentSettings = currentSettings.copy(enableNotifications = enabled)
                                }
                            },
                            onAnalyticsChange = { enabled ->
                                viewModel.viewModelScope.launch {
                                    settingsRepository.saveAnalyticsEnabled(enabled)
                                    currentSettings = currentSettings.copy(enableAnalytics = enabled)
                                }
                            },
                            onSyncFrequencyChange = { frequency ->
                                viewModel.viewModelScope.launch {
                                    settingsRepository.saveSyncFrequency(frequency)
                                    currentSettings = currentSettings.copy(syncFrequency = frequency)
                                }
                            },
                            onShowSyncTimestampChange = { enabled ->
                                // Dispatch intent to ViewModel for reactive UI update across all screens
                                viewModel.processIntent(ProductsIntent.SetShowSyncTimestamp(enabled))
                                // Also update local state for immediate Settings screen update
                                currentSettings = currentSettings.copy(showSyncTimestamp = enabled)
                            },
                            onResetToDefaults = {
                                viewModel.viewModelScope.launch {
                                    settingsRepository.resetToDefaults()
                                    val newSettings = settingsRepository.getSettings()
                                    currentSettings = newSettings
                                    // Update ViewModel state with reset values
                                    viewModel.processIntent(ProductsIntent.SetViewLayoutMode(newSettings.viewLayoutPreference))
                                    viewModel.processIntent(ProductsIntent.SetShowSyncTimestamp(newSettings.showSyncTimestamp))
                                }
                            }
                        )
                    }
                    entry<Route.Delivery> {
                        DeliveryScreen(
                            onProceedToPayment = { address ->
                                viewModel.processIntent(
                                    intent = ProductsIntent.SetDeliveryAddress(address = address)
                                )
                                navigator.navigate(route = Route.Payment)
                            }
                        )
                    }
                    entry<Route.Payment> {
                        PaymentScreen(
                            totalAmount = state.totalRetailPrice,
                            onConfirmPayment = { paymentMethod ->
                                viewModel.processIntent(
                                    intent = ProductsIntent.SetPaymentMethod(paymentMethod)
                                )
                                viewModel.processIntent(
                                    intent = ProductsIntent.ConfirmOrder
                                )
                            }
                        )
                    }
                    entry<Route.OrderTracking> {
                        val order = state.orders.findLast { order ->
                            order.id == it.orderId
                        }
                        OrderTrackingScreen(
                            order = order,
                            onNavigateToRating = {
                                order?.let { o ->
                                    navigator.navigate(route = Route.OrderRating(orderId = o.id))
                                }
                            }
                        )
                    }
                    entry<Route.OrderRating> {
                        val order = state.orders.findLast { order ->
                            order.id == it.orderId
                        }
                        OrderRatingScreen(
                            order = order,
                            onSubmitRatings = { ratingsMap ->
                                ratingsMap.forEach { (productId, ratingAndComment) ->
                                    viewModel.processIntent(
                                        intent = ProductsIntent.RateProduct(
                                            orderId = it.orderId,
                                            productId = productId,
                                            rating = ratingAndComment.first,
                                            comment = ratingAndComment.second
                                        )
                                    )
                                }
                                navigator.goBack()
                            }
                        )
                    }
                }
            )
        )
    }
}
