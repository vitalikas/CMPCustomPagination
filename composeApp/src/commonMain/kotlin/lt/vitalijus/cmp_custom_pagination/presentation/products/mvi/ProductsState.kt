package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Single immutable state for the Products feature.
 */
data class ProductsState(
    val products: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList(),
    val favoriteProductIds: Set<Long> = emptySet(),
    val isLoadingMore: Boolean = false,
    val error: String? = null
) {
    // Computed properties (derived state)
    val totalQuantity: Int
        get() = basketItems.sumOf { it.quantity }

    val totalCostPrice: Double
        get() = basketItems.sumOf { it.product.price * 0.7 * it.quantity }

    val totalRetailPrice: Double
        get() = basketItems.sumOf { it.product.price * it.quantity }

    val isBasketEmpty: Boolean
        get() = basketItems.isEmpty()

    val favoriteProducts: List<Product>
        get() = products.filter { it.id in favoriteProductIds }
}
