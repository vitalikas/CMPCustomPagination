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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lt.vitalijus.cmp_custom_pagination.di.AppKoinComponent
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen
import org.koin.core.component.inject

@Composable
fun ProductScreen() {
    val viewModel: ProductsViewModel by AppKoinComponent.inject()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState =
        rememberSaveable(saver = LazyListState.Saver) {
            LazyListState()
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state.currentScreen) {
                            Screen.BASKET -> "Shopping Basket"
                            Screen.PRODUCT_LIST -> "Product List"
                        }
                    )
                }
            )
        }
    ) { contentPadding ->
        when (state.currentScreen) {
            Screen.BASKET -> {
                BasketScreen(
                    basketState = state.basketState,
                    onNavigateToProducts = { viewModel.navigateToScreen(Screen.PRODUCT_LIST) },
                    onRemoveItem = { viewModel.removeFromBasket(it) },
                    onClearBasket = { viewModel.clearBasket() },
                    onUpdateQuantity = { productId, quantity ->
                        viewModel.updateQuantity(
                            productId,
                            quantity
                        )
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }

            Screen.PRODUCT_LIST -> {
                ProductListScreen(
                    productListState = state.productListState,
                    basketState = state.basketState,
                    onNavigateBack = { viewModel.navigateToScreen(Screen.BASKET) },
                    onAddToBasket = { product, quantity ->
                        viewModel.addToBasket(
                            product,
                            quantity
                        )
                    },
                    onLoadMore = { viewModel.loadNextProducts() },
                    lazyListState = lazyListState,
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    }
}
