package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.basket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.BasketItemCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkHorizontally

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketScreen(
    state: ProductsState,
    onIntent: (ProductsIntent) -> Unit,
    onProductClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.isBasketEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.ShoppingCart,
                        contentDescription = "Empty basket",
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Your Basket is Empty",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add products to your basket to continue shopping",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.basketItems, key = { it.product.id }) { basketItem ->
                    AnimatedVisibility(
                        visible = state.basketItems.any { it.product.id == basketItem.product.id },
                        enter = fadeIn(),
                        exit = fadeOut() + shrinkHorizontally(),
                        modifier = Modifier
                    ) {
                        BasketItemCard(
                            basketItem = basketItem,
                            onUpdateQuantity = { newQuantity ->
                                onIntent(
                                    ProductsIntent.UpdateQuantity(
                                        productId = basketItem.product.id,
                                        newQuantity = newQuantity
                                    )
                                )
                            },
                            onRemove = {
                                onIntent(ProductsIntent.RemoveProduct(basketItem.product.id))
                            },
                            onItemClick = {
                                onProductClick(basketItem.product.id)
                            }
                        )
                    }
                }
            }

            // Basket summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Items:")
                    Text(text = "${state.totalQuantity}")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Price:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatPrice(state.totalRetailPrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onIntent(ProductsIntent.NavigateTo(lt.vitalijus.cmp_custom_pagination.presentation.products.Screen.Delivery)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Proceed to Checkout")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onIntent(ProductsIntent.ClearBasket) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Basket")
                }
            }
        }
    }
}
