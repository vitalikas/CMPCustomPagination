package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

private enum class LayoutMode {
    GRID, LIST
}

@Composable
fun FavoritesScreen(
    state: ProductsState,
    onProductClick: (Long) -> Unit = {},
    onFavoriteClick: (Long) -> Unit = {},
    selectedProductId: Long? = null,
    onIntent: (ProductsIntent) -> Unit = {}
) {
    // Layout mode is now in shared state - no local state needed!
    val layoutMode = when (state.viewLayoutMode) {
        ViewLayoutPreference.GRID -> LayoutMode.GRID
        ViewLayoutPreference.LIST -> LayoutMode.LIST
    }

    // Always load fresh data from network when entering favorites screen
    // Load favorites by IDs using parallel network calls
    LaunchedEffect(state.favoriteProductIds) {
        onIntent(ProductsIntent.LoadFavorites(state.favoriteProductIds))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            // Case 1: Loading data from network
            state.isLoadingFavorites -> {
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
                            text = "Loading favorites...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Case 2: No favorites found
            state.favoriteProductsData.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Favorite,
                            contentDescription = "Favorites",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "No Favorites Yet",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Add products to your favorites\nby tapping the heart icon",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Case 3: Have favorites and they're loaded
            else -> {
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
                    allItemsLoaded = true, // Favorites are always fully loaded
                    loadedItemsCount = state.favoriteProductsData.size,
                    onSortOptionChange = { sortOption ->
                        onIntent(ProductsIntent.SetSortOption(sortOption))
                    },
                    onLoadAllItems = {
                        // No-op for favorites since they're always fully loaded
                    }
                )

                // Search bar
                ProductSearchBar(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = { query ->
                        onIntent(ProductsIntent.SearchProducts(query))
                    },
                    placeholder = "Search favorites..."
                )

                // Show "No results found" if search is active and no results
                if (state.searchQuery.isNotBlank() && state.filteredFavoriteProducts.isEmpty()) {
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
                } else {
                    // Display content based on selected layout mode
                    when (layoutMode) {
                        LayoutMode.GRID -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.filteredFavoriteProducts) { product ->
                                    ProductCard(
                                        product = product,
                                        isFavorite = true,
                                        isSelected = selectedProductId == product.id,
                                        onProductClick = onProductClick,
                                        onFavoriteClick = onFavoriteClick
                                    )
                                }
                            }
                        }

                        LayoutMode.LIST -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.filteredFavoriteProducts) { product ->
                                    ProductCardHorizontal(
                                        product = product,
                                        isFavorite = true,
                                        isSelected = selectedProductId == product.id,
                                        onProductClick = onProductClick,
                                        onFavoriteClick = onFavoriteClick
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
