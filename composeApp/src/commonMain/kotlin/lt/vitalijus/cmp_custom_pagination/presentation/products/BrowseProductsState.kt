package lt.vitalijus.cmp_custom_pagination.presentation.products

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

data class ProductsState(
    val browseProductsState: BrowseProductsState = BrowseProductsState(),
    val basketState: BasketState = BasketState()
)

data class BrowseProductsState(
    val products: List<Product> = emptyList(),
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

data class BasketState(
    val items: List<BasketItem> = emptyList()
) {
    val totalQuantity: Int
        get() = items.sumOf { it.quantity }

    val totalCostPrice: Double
        get() = items.sumOf { it.totalCost }

    val totalRetailPrice: Double
        get() = items.sumOf { it.totalRetail }

    val isEmpty: Boolean
        get() = items.isEmpty()
}
