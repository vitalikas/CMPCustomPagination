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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import lt.vitalijus.cmp_custom_pagination.di.AppKoinComponent
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsEffect
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.ScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes.ListDetailScene
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes.rememberListDetailSceneStrategy
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
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.Products,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys,
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

    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

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
            val showBackButton = when (currentRoute) {
                Route.Products -> false
                Route.Favorites -> false
                Route.Basket -> false
                Route.Orders -> false
                is Route.ProductDetail -> true
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
                }
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
            onBack = navigator::goBack,
            sceneStrategy = rememberListDetailSceneStrategy(),
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
                            }
                        )
                    }
                    entry<Route.ProductDetail>(
                        metadata = ListDetailScene.detailPane()
                    ) { it ->
                        val product = state.products.find { product ->
                            product.id == it.productId
                        } ?: return@entry

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
                    entry<Route.Favorites> {
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
                    entry<Route.Delivery> {
                        DeliveryScreen(
                            onProceedToPayment = { address ->
                                viewModel.processIntent(
                                    intent = ProductsIntent.SetDeliveryAddress(address)
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
