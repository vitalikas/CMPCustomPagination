package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.ProductCard

@Composable
fun FavoritesScreen(
    state: ProductsState,
    onProductClick: (Long) -> Unit = {},
    onFavoriteClick: (Long) -> Unit = {},
    onIntent: (ProductsIntent) -> Unit = {}
) {
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
                        androidx.compose.material3.CircularProgressIndicator()
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
                // Grid of favorite products
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.favoriteProductsData) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = true,
                            onProductClick = onProductClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }
                }
            }
        }
    }
}
