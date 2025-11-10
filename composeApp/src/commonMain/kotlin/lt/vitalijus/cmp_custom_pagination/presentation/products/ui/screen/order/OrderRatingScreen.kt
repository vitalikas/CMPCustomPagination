package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons

@Composable
fun OrderRatingScreen(
    order: Order?,
    onSubmitRatings: (Map<Long, Pair<Int, String>>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (order == null) {
        Text("Order not found")
        return
    }

    val ratings = remember { mutableStateMapOf<Long, Int>() }
    val comments = remember { mutableStateMapOf<Long, String>() }

    // Initialize with existing ratings
    order.items.forEach { item ->
        val productId = item.product.id
        if (productId !in ratings) {
            val existingRating = order.ratings[productId]
            ratings[productId] = existingRating?.rating ?: 0
            comments[productId] = existingRating?.comment ?: ""
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Rate Your Products",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(order.items) { item ->
            val productId = item.product.id
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = item.product.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Star rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = AppIcons.StarRateHalf,
                                contentDescription = "$star stars",
                                tint = if (star <= (ratings[productId] ?: 0)) {
                                    Color(0xFFFFC107)
                                } else {
                                    Color.Gray.copy(alpha = 0.3f)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        ratings[productId] = star
                                    }
                            )
                        }
                    }

                    // Comment field
                    OutlinedTextField(
                        value = comments[productId] ?: "",
                        onValueChange = { comments[productId] = it },
                        label = { Text("Your comment") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val ratingsMap = order.items.associate { item ->
                        val productId = item.product.id
                        productId to Pair(
                            ratings[productId] ?: 0,
                            comments[productId] ?: ""
                        )
                    }.filterValues { it.first > 0 } // Only include rated products

                    onSubmitRatings(ratingsMap)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = ratings.values.any { it > 0 }
            ) {
                Text("Submit Ratings")
            }
        }
    }
}
