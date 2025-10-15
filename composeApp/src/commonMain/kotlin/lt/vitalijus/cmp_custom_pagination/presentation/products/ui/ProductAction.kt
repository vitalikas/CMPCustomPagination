package lt.vitalijus.cmp_custom_pagination.presentation.products.ui

import lt.vitalijus.cmp_custom_pagination.domain.model.Product

sealed interface ProductAction {

    data class AddToBasket(
        val product: Product,
        val count: Int
    ) : ProductAction
    data object LoadMore : ProductAction
    data class RemoveProduct(val productId: Long) : ProductAction
    data object ClearBasket : ProductAction
    data class UpdateQuantity(
        val productId: Long,
        val newQuantity: Int
    ) : ProductAction
}
