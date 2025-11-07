package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.Review
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.QuantityControl

@Composable
fun ProductDetailsScreen(
    product: Product,
    isFavorite: Boolean = false,
    onAddToBasket: (Int) -> Unit,
    onNavigateToBasket: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    var quantity by remember { mutableStateOf(1) }
    var quantityText by remember { mutableStateOf("1") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable content takes 80% of screen height
            Box(modifier = Modifier.weight(0.8f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Image Gallery with Favorite Button
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (!product.images.isNullOrEmpty()) {
                                val pagerState =
                                    rememberPagerState(pageCount = { product.images.size })
                                Column {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    ) { page ->
                                        AsyncImage(
                                            model = product.images[page],
                                            contentDescription = product.title,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    // Only show indicator when there's more than one image
                                    if (product.images.size > 1) {
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            (0 until product.images.size).forEach { index ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            color = if (pagerState.currentPage == index) {
                                                                MaterialTheme.colorScheme.primary
                                                            } else {
                                                                MaterialTheme.colorScheme.onSurface.copy(
                                                                    alpha = 0.5f
                                                                )
                                                            },
                                                            shape = CircleShape
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            } else if (product.thumbnail != null) {
                                AsyncImage(
                                    model = product.thumbnail,
                                    contentDescription = product.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Favorite button inside the image card
                            Card(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                ),
                                shape = CircleShape
                            ) {
                                IconButton(
                                    onClick = onFavoriteClick
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) AppIcons.Favorite else AppIcons.FavoriteBorder,
                                        contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                                        tint = if (isFavorite) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Title and Brand
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            product.brand?.let {
                                Text(
                                    text = "Brand: $it",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Price and Rating
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = formatPrice(product.retailPrice),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                product.discountPercentage?.let { discount ->
                                    Text(
                                        text = "${discount.toInt()}% OFF",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Green
                                    )
                                }
                            }

                            product.rating?.let { rating ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = AppIcons.StarRateHalf,
                                        contentDescription = "Star",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "$rating",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Stock and Availability
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            product.stock?.let { stock ->
                                Text(
                                    text = "Stock: $stock units",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            product.availabilityStatus?.let { status ->
                                Text(
                                    text = status,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (status.contains("stock", ignoreCase = true))
                                        Color.Green else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Description
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                product.description?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    // Tags
                    if (!product.tags.isNullOrEmpty()) {
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Tags",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(product.tags) { tag ->
                                        AssistChip(
                                            onClick = { },
                                            label = { Text(tag) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Product Information
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Product Information",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                product.category?.let {
                                    InfoRow("Category", it)
                                }
                                product.sku?.let {
                                    InfoRow("SKU", it)
                                }
                                product.weight?.let {
                                    InfoRow("Weight", "$it kg")
                                }
                                product.dimensions?.let { dim ->
                                    InfoRow(
                                        "Dimensions",
                                        "${dim.width} × ${dim.height} × ${dim.depth} cm"
                                    )
                                }
                                product.minimumOrderQuantity?.let {
                                    InfoRow("Min. Order", "$it units")
                                }
                            }
                        }
                    }

                    // Shipping & Warranty
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Shipping & Warranty",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                product.shippingInformation?.let {
                                    InfoRow("Shipping", it)
                                }
                                product.warrantyInformation?.let {
                                    InfoRow("Warranty", it)
                                }
                                product.returnPolicy?.let {
                                    InfoRow("Returns", it)
                                }
                            }
                        }
                    }

                    // Reviews
                    if (!product.reviews.isNullOrEmpty()) {
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Customer Reviews (${product.reviews.size})",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                product.reviews.forEach { review ->
                                    ReviewCard(review = review)
                                }
                            }
                        }
                    }
                }
            }

            // Sticky bottom section
            Card(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = formatPrice(product.retailPrice * quantity),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Quantity Controls and Add to Basket Button in one row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity Controls
                        QuantityControl(
                            quantity = quantity,
                            onQuantityChange = { newQuantity ->
                                quantity = newQuantity
                                quantityText = newQuantity.toString()
                            },
                            buttonBackgroundColor = Color(0xFF1B5E20).copy(alpha = 0.1f)
                        )

                        // Add to Basket Button
                        Button(
                            onClick = {
                                onAddToBasket(quantity)
                                onNavigateToBasket()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Add to Basket",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.reviewerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.StarRateHalf,
                        contentDescription = "Star",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${review.rating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = review.date.substring(0, 10), // Just show date part
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
