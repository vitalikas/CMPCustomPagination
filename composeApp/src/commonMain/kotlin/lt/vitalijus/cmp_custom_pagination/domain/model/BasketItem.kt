package lt.vitalijus.cmp_custom_pagination.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BasketItem(
    val product: Product,
    val quantity: Int
) {
    val totalCost: Double
        get() = product.costPrice * quantity

    val totalRetail: Double
        get() = product.retailPrice * quantity
}
