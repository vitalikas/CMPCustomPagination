@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.NavigationManager
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.NavigationManagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.ScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.NavigationBottomBar
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

    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    Scaffold(
        topBar = {
            val currentRoute = currentEntry?.destination?.route
            val title = screenTitleProvider.getTitleForRoute(currentRoute)

            TopAppBar(
                title = {
                    Text(text = title)
                }
            )
        },
        bottomBar = {
            val currentRoute = currentEntry?.destination?.route
            val currentScreen = when {
                currentRoute?.contains("ProductList") == true -> Screen.ProductList
                currentRoute?.contains("Basket") == true -> Screen.Basket
                else -> Screen.ProductList
            }

            NavigationBottomBar(
                onNavigateToScreen = { screen ->
                    navigationManager.navigateToScreen(screen)
                },
                currentScreen = currentScreen,
                basketNotEmpty = !state.basketState.isEmpty,
                basketQuantity = state.basketState.totalQuantity
            )
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            state = state,
            lazyListState = lazyListState,
            onAddToBasket = viewModel::addToBasket,
            onLoadMore = viewModel::loadNextProducts,
            onRemoveItem = viewModel::removeFromBasket,
            onClearBasket = viewModel::clearBasket,
            onUpdateQuantity = viewModel::updateQuantity,
            onNavigateToProducts = { navigationManager.navigateToScreen(Screen.ProductList) },
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
