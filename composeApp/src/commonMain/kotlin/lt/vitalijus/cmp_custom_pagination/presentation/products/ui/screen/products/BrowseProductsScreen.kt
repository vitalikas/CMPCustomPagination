package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.presentation.products.BasketState
import lt.vitalijus.cmp_custom_pagination.presentation.products.BrowseProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.ProductAction
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductCard

@Composable
fun ProductListScreen(
    browseProductsState: BrowseProductsState,
    basketState: BasketState,
    onAction: (ProductAction) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (!basketState.isEmpty) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Basket: ${basketState.totalQuantity} items - ${formatPrice(basketState.totalRetailPrice)}",
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LaunchedEffect(Unit) {
            if (browseProductsState.products.isEmpty()) {
                onAction(ProductAction.LoadMore) // Load initial products
            }
        }

        LaunchedEffect(
            lazyListState,
            browseProductsState.products.size,
            browseProductsState.isLoadingMore
        ) {
            snapshotFlow { lazyListState.layoutInfo }
                .distinctUntilChanged()
                .collect { layoutInfo ->
                    val totalItemsCount = browseProductsState.products.size
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                    val shouldPaginate =
                        lastVisibleIndex >= totalItemsCount - 3 // Trigger when 3 items from the end
                                && totalItemsCount > 0
                                && !browseProductsState.isLoadingMore
                    if (shouldPaginate) {
                        onAction(ProductAction.LoadMore)
                    }
                }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(browseProductsState.products) { product ->
                ProductCard(
                    product = product,
                    onAddToBasket = { quantity ->
                        onAction(
                            ProductAction.AddToBasket(
                                product = product,
                                count = quantity
                            )
                        )
                    }
                )
            }

            if (browseProductsState.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
