package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

@Composable
fun ProductCard(
    product: Product,
    onAddToBasket: (Int) -> Unit
) {
    val (quantity, setQuantity) = remember { mutableStateOf(1) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            product.description?.let { description ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatPrice(product.retailPrice),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    product.category?.let { category ->
                        Text(
                            text = category.uppercase(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            4.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp),
                            onClick = { if (quantity > 1) setQuantity(quantity - 1) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-")
                        }
                        Text(
                            text = quantity.toString(),
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
                        )
                        OutlinedButton(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp),
                            onClick = { setQuantity(quantity + 1) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAddToBasket(quantity) }
                    ) {
                        Text("Add to Basket")
                    }
                }
            }
        }
    }
}
