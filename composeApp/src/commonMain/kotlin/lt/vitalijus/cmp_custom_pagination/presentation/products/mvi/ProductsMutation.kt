package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import kotlin.jvm.JvmInline

/**
 * State mutations - describe HOW the state should change.
 * The mutation type itself conveys the state transition.
 */
sealed interface ProductsMutation {

    // Loading mutations
    @JvmInline
    value class SetLoading(
        val isLoading: Boolean
    ) : ProductsMutation

    // Product mutations
    @JvmInline
    value class ProductsLoaded(
        val products: List<Product>
    ) : ProductsMutation

    @JvmInline
    value class LoadingError(
        val message: String
    ) : ProductsMutation

    // Basket mutations
    @JvmInline
    value class BasketUpdated(
        val items: List<BasketItem>
    ) : ProductsMutation

    // Favorite mutations
    @JvmInline
    value class FavoriteToggled(
        val productId: Long
    ) : ProductsMutation
}
