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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.data.persistence.SettingsRepository
import lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.NetworkStatusBanner
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductCard
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductCardHorizontal
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductSearchBar
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductsToolbar

@Composable
fun ProductsScreen(
    state: ProductsState,
    onIntent: (ProductsIntent) -> Unit,
    onProductClick: (Long) -> Unit = {},
    onFavoriteClick: (Long) -> Unit = {},
    selectedProductId: Long? = null,
    syncIntervalMs: Long? = null,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()
    
    // Layout mode is now in shared state - no local state needed!
    val layoutMode = when (state.viewLayoutMode) {
        ViewLayoutPreference.GRID -> LayoutMode.GRID
        ViewLayoutPreference.LIST -> LayoutMode.LIST
    }

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
        // Network status banner (appears when offline)
        NetworkStatusBanner(isConnected = state.isConnectedToInternet)
        
        // Unified toolbar (View + Sort in one row)
        ProductsToolbar(
            isGridLayout = layoutMode == LayoutMode.GRID,
            onLayoutToggle = {
                // Toggle layout mode via intent (automatically saved)
                val newMode = when (state.viewLayoutMode) {
                    ViewLayoutPreference.GRID -> ViewLayoutPreference.LIST
                    ViewLayoutPreference.LIST -> ViewLayoutPreference.GRID
                }
                onIntent(ProductsIntent.SetViewLayoutMode(newMode))
            },
            currentSortOption = state.sortOption,
            allItemsLoaded = state.allItemsLoaded,
            loadedItemsCount = state.products.size,
            onSortOptionChange = { sortOption ->
                onIntent(ProductsIntent.SetSortOption(sortOption))
            },
            onLoadAllItems = {
                onIntent(ProductsIntent.LoadAllItems)
            },
            onManualRefresh = {
                onIntent(ProductsIntent.ManualRefresh)
            },
            lastSyncTimestamp = state.lastSyncTimestamp,
            showSyncInfo = state.showSyncTimestamp, // Reactive to settings
            isRefreshing = state.isRefreshing,
            syncIntervalMs = syncIntervalMs // Show rotating icon during refresh
        )

        // Search bar
        ProductSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = { query ->
                onIntent(ProductsIntent.SearchProducts(query))
            }
        )

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

        // Show loading overlay when loading all items
        if (state.isLoadingAllItems) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading all items...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Loaded ${state.products.size} items",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Images will load as you scroll",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LaunchedEffect(
            layoutMode,
            state.products.size,
            state.isLoadingMore,
            state.sortOption,
            state.isLoadingAllItems
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
                        
                        // Disable pagination when:
                        // 1. Sorting is active (except NONE)
                        // 2. Currently loading all items
                        // 3. All items already loaded
                        val paginationEnabled = state.sortOption == lt.vitalijus.cmp_custom_pagination.domain.model.SortOption.NONE
                                && !state.isLoadingAllItems
                                && !state.allItemsLoaded
                        
                        val shouldPaginate =
                            lastVisibleIndex >= totalItemsCount - 3
                                    && totalItemsCount > 0
                                    && !state.isLoadingMore
                                    && paginationEnabled
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

        // Show "No results found" if search is active and no results
        if (state.searchQuery.isNotBlank() && state.filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.Search,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try a different search term",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
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
                    items(state.filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = state.favoriteProductIds.contains(product.id),
                            isSelected = selectedProductId == product.id,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }

                    if (state.isLoadingMore && state.searchQuery.isBlank()) {
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
                    items(state.filteredProducts) { product ->
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
