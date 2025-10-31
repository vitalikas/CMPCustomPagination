@file:OptIn(ExperimentalMaterial3Api::class)

package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import lt.vitalijus.cmp_custom_pagination.presentation.products.BasketState
import lt.vitalijus.cmp_custom_pagination.presentation.products.BrowseProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModelMvi
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsEffect
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationManager
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationManagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.ScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.NavigationBottomBar
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.basket.BasketScreen
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.products.ProductListScreen
import org.koin.core.component.inject

@Composable
fun RootScreen() {
    val viewModel: ProductsViewModelMvi by AppKoinComponent.inject()

    val screenTitleProvider: ScreenTitleProvider by AppKoinComponent.inject()

    val navigationManagerFactory: NavigationManagerFactory by AppKoinComponent.inject()

    val navController = rememberNavController()

    val currentEntry by navController.currentBackStackEntryAsState()

    val navigationManager: NavigationManager = remember(navController) {
        navigationManagerFactory.create(navController)
    }

    val mviState by viewModel.state.collectAsStateWithLifecycle()

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

                is ProductsEffect.NavigateTo -> {
                    navigationManager.navigateToScreen(effect.screen)
                }

                is ProductsEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val state = ProductsState(
        browseProductsState = BrowseProductsState(
            products = mviState.products,
            isLoadingMore = mviState.isLoadingMore,
            error = mviState.error
        ),
        basketState = BasketState(
            items = mviState.basketItems
        )
    )

    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
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
                    viewModel.processIntent(ProductsIntent.NavigateTo(screen))
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
            onIntent = viewModel::processIntent,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    state: ProductsState,
    lazyListState: LazyListState,
    onIntent: (ProductsIntent) -> Unit,
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
                onIntent = onIntent,
                lazyListState = lazyListState
            )
        }

        composable<Screen.Basket> {
            BasketScreen(
                basketState = state.basketState,
                onIntent = onIntent
            )
        }
    }
}
