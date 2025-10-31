package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

sealed interface ProductsIntent {

    data object LoadMore : ProductsIntent
    data class AddToBasket(
        val product: Product,
        val quantity: Int
    ) : ProductsIntent
    data class UpdateQuantity(
        val productId: Long,
        val newQuantity: Int
    ) : ProductsIntent
    data class RemoveProduct(val productId: Long) : ProductsIntent
    data object ClearBasket : ProductsIntent
    data class NavigateTo(val screen: Screen) : ProductsIntent
}
