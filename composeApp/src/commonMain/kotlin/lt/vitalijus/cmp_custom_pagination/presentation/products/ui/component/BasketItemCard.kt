package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun BasketItemCard(
    basketItem: BasketItem,
    onRemove: () -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onItemClick: () -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    val maxReveal = 120f
    val revealColor = Color(0xFFFFCDD2) // Material Red 100
    // Only reveal right (left-to-right swipe)
    val revealEdge = offsetX > 0

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Transparent)
            .clipToBounds()
    ) {
        val iconSize = 40.dp
        val iconPad = 24.dp
        val density = LocalDensity.current
        if (revealEdge) {
            BoxWithConstraints(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(revealColor)
            ) {
                val widthPx = this.constraints.maxWidth
                val iconPx = with(density) { iconSize.toPx() }
                val padPx = with(density) { iconPad.toPx() }
                // Place icon at actual card's left edge as it moves, but don't exceed visible area
                val iconX = min(
                    offsetX - padPx - iconPx,
                    widthPx - padPx - iconPx
                ).coerceAtLeast(padPx)
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(iconSize)
                        .offset { IntOffset(iconX.toInt(), 0) }
                        .align(Alignment.CenterStart)
                )
            }
        }
        Card(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt().coerceAtLeast(0), 0)
                }
                .pointerInput(basketItem.product.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > maxReveal * 0.9f) {
                                onRemove()
                                offsetX = 0f
                            } else {
                                offsetX = 0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceAtLeast(0f)
                        }
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.elevatedCardElevation(0.dp),
            onClick = onItemClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Product Image
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    ) {
                        basketItem.product.thumbnail?.let { thumbnailUrl ->
                            AsyncImage(
                                model = thumbnailUrl,
                                contentDescription = basketItem.product.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Product details with title, price, quantity, and delete button
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Title
                        Text(
                            text = basketItem.product.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Price
                        Text(
                            text = "${formatPrice(basketItem.product.retailPrice)} each",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Description
                        basketItem.product.description?.let { description ->
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Controls row: Quantity Control and Delete button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            QuantityControl(
                                quantity = basketItem.quantity,
                                onQuantityChange = onUpdateQuantity,
                                buttonSize = 32.dp,
                                textFieldWidth = 45.dp,
                                buttonBackgroundColor = Color(
                                    0xFF1B5E20
                                )
                                    .copy(alpha = 0.1f)
                            )

                            TextButton(
                                onClick = onRemove,
                                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                    containerColor = Color(0xFF1B5E20)
                                        .copy(alpha = 0.1f)
                                )
                            ) {
                                Icon(
                                    imageVector = AppIcons.Delete,
                                    contentDescription = "Remove item",
                                    tint = Color(0xFF1B5E20),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
