package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.basket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.presentation.products.BasketState
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.BasketItemCard

@Composable
fun BasketScreen(
    basketState: BasketState,
    onIntent: (ProductsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (basketState.isEmpty) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your basket is empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Use the Products tab to browse and add items",
                    fontSize = 16.sp
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onIntent(ProductsIntent.ClearBasket) }
                ) {
                    Text(text = "Clear Basket")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(basketState.items) { basketItem ->
                    BasketItemCard(
                        basketItem = basketItem,
                        onRemove = {
                            onIntent(ProductsIntent.RemoveProduct(productId = basketItem.product.id))
                        },
                        onUpdateQuantity = { quantity ->
                            onIntent(
                                ProductsIntent.UpdateQuantity(
                                    productId = basketItem.product.id,
                                    newQuantity = quantity
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Items:",
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = "${basketState.totalQuantity}")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Price:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = formatPrice(basketState.totalRetailPrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
