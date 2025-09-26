@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.cmp_custom_pagination.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lt.vitalijus.cmp_custom_pagination.di.AppKoinComponent
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.NavigationBottomBar
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.BasketScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.ProductListScreen
import org.koin.core.component.inject

@Composable
fun AppNavigation() {
    val viewModel: ProductsViewModel by AppKoinComponent.inject()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val currentEntry by navController.currentBackStackEntryAsState()
                    val currentScreen = when (currentEntry?.destination?.route) {
                        "lt.vitalijus.cmp_custom_pagination.presentation.products.Screen.Basket" -> "Shopping Basket"
                        "lt.vitalijus.cmp_custom_pagination.presentation.products.Screen.ProductList" -> "Product List"
                        else -> "Unknown screen"
                    }
                    Text(text = currentScreen)
                }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                onNavigateToScreen = { screen ->
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route,
                basketNotEmpty = !state.basketState.isEmpty,
                basketQuantity = state.basketState.totalQuantity
            )
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            state = state,
            lazyListState = lazyListState,
            onAddToBasket = { product, quantity ->
                viewModel.addToBasket(product, quantity)
            },
            onLoadMore = { viewModel.loadNextProducts() },
            onRemoveItem = { viewModel.removeFromBasket(it) },
            onClearBasket = { viewModel.clearBasket() },
            onUpdateQuantity = { productId, quantity ->
                viewModel.updateQuantity(productId, quantity)
            },
            onNavigateToProducts = {
                navController.navigate(Screen.ProductList) {
                    popUpTo(Screen.ProductList) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    state: ProductsState,
    lazyListState: LazyListState,
    onAddToBasket: (Product, Int) -> Unit,
    onLoadMore: () -> Unit,
    onRemoveItem: (Long) -> Unit,
    onClearBasket: () -> Unit,
    onUpdateQuantity: (Long, Int) -> Unit,
    onNavigateToProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ProductList,
        modifier = modifier
    ) {
        composable<Screen.ProductList> {
            ProductListScreen(
                browseProductsState = state.browseProductsState,
                basketState = state.basketState,
                onAddToBasket = onAddToBasket,
                onLoadMore = onLoadMore,
                lazyListState = lazyListState
            )
        }

        composable<Screen.Basket> {
            BasketScreen(
                basketState = state.basketState,
                onNavigateToProducts = onNavigateToProducts,
                onRemoveItem = onRemoveItem,
                onClearBasket = onClearBasket,
                onUpdateQuantity = onUpdateQuantity
            )
        }
    }
}
