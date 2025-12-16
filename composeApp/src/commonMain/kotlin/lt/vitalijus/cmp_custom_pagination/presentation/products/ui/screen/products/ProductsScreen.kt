package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductCard
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductCardHorizontal

@Composable
fun ProductsScreen(
    state: ProductsState,
    onIntent: (ProductsIntent) -> Unit,
    onProductClick: (Long) -> Unit = {},
    onFavoriteClick: (Long) -> Unit = {},
    selectedProductId: Long? = null,
    modifier: Modifier = Modifier
) {
    var layoutMode by remember { mutableStateOf(LayoutMode.GRID) }
    val lazyGridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()

    // Synchronize scroll position when switching layouts
    LaunchedEffect(layoutMode) {
        when (layoutMode) {
            LayoutMode.GRID -> {
                // When switching to grid, scroll to the first visible item from list
                val listFirstVisibleIndex = lazyListState.firstVisibleItemIndex
                if (listFirstVisibleIndex > 0) {
                    lazyGridState.scrollToItem(listFirstVisibleIndex)
                }
            }
            LayoutMode.LIST -> {
                // When switching to list, scroll to the first visible item from grid
                val gridFirstVisibleIndex = lazyGridState.firstVisibleItemIndex
                if (gridFirstVisibleIndex > 0) {
                    lazyListState.scrollToItem(gridFirstVisibleIndex)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with basket info and layout toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!state.isBasketEmpty) {
                Text(
                    text = "Basket: ${state.totalQuantity} items - ${formatPrice(state.totalRetailPrice)}",
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Products",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Layout toggle button
            IconButton(onClick = {
                layoutMode = if (layoutMode == LayoutMode.GRID) LayoutMode.LIST else LayoutMode.GRID
            }) {
                Icon(
                    imageVector = if (layoutMode == LayoutMode.GRID) AppIcons.ViewList else AppIcons.GridView,
                    contentDescription = if (layoutMode == LayoutMode.GRID) "Switch to list view" else "Switch to grid view"
                )
            }
        }

        LaunchedEffect(Unit) {
            if (state.products.isEmpty()) {
                onIntent(ProductsIntent.LoadMore) // Load initial products
            }
        }

        // Show centered progress bar on initial load
        if (state.products.isEmpty() && state.isLoadingMore) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        LaunchedEffect(
            layoutMode,
            state.products.size,
            state.isLoadingMore
        ) {
            suspend fun <T> monitorScrollForPagination(
                scrollFlow: kotlinx.coroutines.flow.Flow<T>,
                getLastVisibleIndex: (T) -> Int?
            ) {
                scrollFlow
                    .distinctUntilChanged()
                    .collect { layoutInfo ->
                        val totalItemsCount = state.products.size
                        val lastVisibleIndex = getLastVisibleIndex(layoutInfo) ?: -1
                        val shouldPaginate =
                            lastVisibleIndex >= totalItemsCount - 3
                                    && totalItemsCount > 0
                                    && !state.isLoadingMore
                        if (shouldPaginate) {
                            onIntent(ProductsIntent.LoadMore)
                        }
                    }
            }

            when (layoutMode) {
                LayoutMode.GRID -> {
                    monitorScrollForPagination(
                        snapshotFlow { lazyGridState.layoutInfo }
                    ) { it.visibleItemsInfo.lastOrNull()?.index }
                }

                LayoutMode.LIST -> {
                    monitorScrollForPagination(
                        snapshotFlow { lazyListState.layoutInfo }
                    ) { it.visibleItemsInfo.lastOrNull()?.index }
                }
            }
        }

        when (layoutMode) {
            LayoutMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.products) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = state.favoriteProductIds.contains(product.id),
                            isSelected = selectedProductId == product.id,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }

                    if (state.isLoadingMore) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.testTag("loading-more-indicator")
                                )
                            }
                        }
                    }
                }
            }

            LayoutMode.LIST -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.products) { product ->
                        ProductCardHorizontal(
                            product = product,
                            isFavorite = state.favoriteProductIds.contains(product.id),
                            isSelected = selectedProductId == product.id,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.testTag("loading-more-indicator")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class LayoutMode {
    GRID, LIST
}
