package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * State mutations - describe HOW the state should change.
 * The mutation type itself conveys the state transition.
 */
sealed interface ProductsMutation {

    // Loading mutations
    data class SetLoading(
        val isLoading: Boolean
    ) : ProductsMutation

    // Product mutations
    data class ProductsLoaded(
        val products: List<Product>
    ) : ProductsMutation

    data class LoadingError(
        val message: String
    ) : ProductsMutation

    // Basket mutations
    data class BasketUpdated(
        val items: List<BasketItem>
    ) : ProductsMutation
}
