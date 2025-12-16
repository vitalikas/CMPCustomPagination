@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen

import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import lt.vitalijus.cmp_custom_pagination.di.AppKoinComponent
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsEffect
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationManager
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationManagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.ScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.NavigationBottomBar
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.basket.BasketScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.details.ProductDetailsScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.DeliveryScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.OrderRatingScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.OrderTrackingScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.OrdersHistoryScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order.PaymentScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.products.ProductsScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.favorites.FavoritesScreen
import org.koin.core.component.inject

@Composable
fun RootScreen() {
    val viewModel: ProductsViewModel by AppKoinComponent.inject()

    val screenTitleProvider: ScreenTitleProvider by AppKoinComponent.inject()

    val navigationManagerFactory: NavigationManagerFactory by AppKoinComponent.inject()

    val navController = rememberNavController()

    val currentEntry by navController.currentBackStackEntryAsState()

    val navigationManager: NavigationManager = remember(navController) {
        navigationManagerFactory.create(navController)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProductsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is ProductsEffect.ShowBasketUpdated -> {
                    // Optionally
                }

                is ProductsEffect.ShowFavoriteToggled -> {
                    // Handled by badge indicator on Favorites tab
                }

                is ProductsEffect.NavigateTo -> {
                    navigationManager.navigateToScreen(effect.screen)
                }

                is ProductsEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                is ProductsEffect.OrderCreated -> {
                    navController.navigate(Screen.OrderTracking(effect.orderId))
                }

                is ProductsEffect.OrderDelivered -> {
                    snackbarHostState.showSnackbar("Your order has been delivered!")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            val currentRoute = currentEntry?.destination?.route
            val title = screenTitleProvider.getTitleForRoute(currentRoute)
            val showBackButton = when {
                currentRoute?.contains("ProductList") == true -> false
                currentRoute?.contains("Basket") == true && navController.previousBackStackEntry != null -> true
                currentRoute?.contains("ProductDetails") == true -> true
                currentRoute?.contains("Favorites") == true -> false
                currentRoute?.contains("Orders") == true && navController.previousBackStackEntry != null -> true
                currentRoute?.contains("Delivery") == true -> true
                currentRoute?.contains("Payment") == true -> true
                currentRoute?.contains("OrderTracking") == true -> true
                currentRoute?.contains("OrderRating") == true -> true
                else -> false
            }

            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showBackButton) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = AppIcons.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            )
        },
        bottomBar = {
            val currentRoute = currentEntry?.destination?.route
            val currentScreen = when {
                currentRoute?.contains("ProductList") == true -> Screen.ProductList
                currentRoute?.contains("ProductDetails") == true -> null // Don't show bottom bar selection for details
                currentRoute?.contains("Basket") == true -> Screen.Basket
                currentRoute?.contains("Favorites") == true -> Screen.Favorites
                currentRoute?.contains("Orders") == true -> Screen.Orders
                currentRoute?.contains("Delivery") == true -> Screen.Orders
                currentRoute?.contains("Payment") == true -> Screen.Orders
                currentRoute?.contains("OrderTracking") == true -> Screen.Orders
                currentRoute?.contains("OrderRating") == true -> Screen.Orders
                else -> Screen.ProductList
            }

            NavigationBottomBar(
                onNavigateToScreen = { screen ->
                    when (screen) {
                        Screen.Basket -> {
                            // Always pop to root then navigate, to fix Basket as fully focused
                            while (navController.previousBackStackEntry != null && navController.currentDestination?.route != null && navController.currentDestination?.route != Screen.Basket.route) {
                                navController.popBackStack()
                            }
                            navController.navigate(Screen.Basket)
                        }
                        Screen.ProductList -> {
                            while (navController.previousBackStackEntry != null && navController.currentDestination?.route != null && navController.currentDestination?.route != Screen.ProductList.route) {
                                navController.popBackStack()
                            }
                            navController.navigate(Screen.ProductList)
                        }
                        else -> {
                            viewModel.processIntent(ProductsIntent.NavigateTo(screen))
                        }
                    }
                },
                currentScreen = currentScreen,
                basketNotEmpty = !state.isBasketEmpty,
                basketQuantity = state.totalQuantity,
                favoritesCount = state.favoriteProductIds.size,
                ordersCount = state.orders.size
            )
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            state = state,
            onIntent = viewModel::processIntent,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    state: ProductsState,
    onIntent: (ProductsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProductList,
        modifier = modifier
    ) {
        composable<Screen.ProductList> {
            ProductsScreen(
                state = state,
                onIntent = onIntent,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails(productId))
                },
                onFavoriteClick = { productId ->
                    onIntent(ProductsIntent.ToggleFavorite(productId))
                }
            )
        }

        composable<Screen.Basket> {
            BasketScreen(
                state = state,
                onIntent = onIntent,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails(productId))
                }
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen(
                state = state,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails(productId))
                },
                onFavoriteClick = { productId ->
                    onIntent(ProductsIntent.ToggleFavorite(productId))
                }
            )
        }

        composable<Screen.ProductDetails> { backStackEntry ->
            val productDetails = backStackEntry.toRoute<Screen.ProductDetails>()
            val product = state.products.firstOrNull { it.id == productDetails.productId }
                ?: state.basketItems.firstOrNull { it.product.id == productDetails.productId }?.product
                ?: state.favoriteProducts.firstOrNull { it.id == productDetails.productId }

            product?.let {
                ProductDetailsScreen(
                    product = it,
                    isFavorite = state.favoriteProductIds.contains(it.id),
                    onAddToBasket = { quantity ->
                        onIntent(
                            ProductsIntent.AddToBasket(
                                product = it,
                                quantity = quantity
                            )
                        )
                    },
                    onNavigateToBasket = {
                        onIntent(ProductsIntent.NavigateTo(Screen.Basket))
                    },
                    onFavoriteClick = {
                        onIntent(ProductsIntent.ToggleFavorite(it.id))
                    }
                )
            }
        }

        composable<Screen.Orders> {
            OrdersHistoryScreen(
                orders = state.orders,
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderTracking(orderId))
                }
            )
        }

        composable<Screen.Delivery> {
            DeliveryScreen(
                onProceedToPayment = { address ->
                    onIntent(ProductsIntent.SetDeliveryAddress(address))
                    navController.navigate(Screen.Payment)
                }
            )
        }

        composable<Screen.Payment> {
            PaymentScreen(
                totalAmount = state.totalRetailPrice,
                onConfirmPayment = { paymentMethod ->
                    onIntent(ProductsIntent.SetPaymentMethod(paymentMethod))
                    onIntent(ProductsIntent.ConfirmOrder)
                }
            )
        }

        composable<Screen.OrderTracking> { backStackEntry ->
            val orderTrackingRoute = backStackEntry.toRoute<Screen.OrderTracking>()
            val order = state.orders.find { it.id == orderTrackingRoute.orderId }

            OrderTrackingScreen(
                order = order,
                onNavigateToRating = {
                    order?.let {
                        navController.navigate(Screen.OrderRating(it.id))
                    }
                }
            )
        }

        composable<Screen.OrderRating> { backStackEntry ->
            val orderRatingRoute = backStackEntry.toRoute<Screen.OrderRating>()
            val order = state.orders.find { it.id == orderRatingRoute.orderId }

            OrderRatingScreen(
                order = order,
                onSubmitRatings = { ratingsMap ->
                    ratingsMap.forEach { (productId, ratingAndComment) ->
                        onIntent(
                            ProductsIntent.RateProduct(
                                orderId = orderRatingRoute.orderId,
                                productId = productId,
                                rating = ratingAndComment.first,
                                comment = ratingAndComment.second
                            )
                        )
                    }
                    navController.popBackStack()
                }
            )
        }
    }
}
