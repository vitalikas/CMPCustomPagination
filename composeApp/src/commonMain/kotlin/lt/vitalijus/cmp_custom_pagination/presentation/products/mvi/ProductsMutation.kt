package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.SortOption
import lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference
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

    @JvmInline
    value class SetLoadingAllItems(
        val isLoading: Boolean
    ) : ProductsMutation

    data object AllItemsLoaded : ProductsMutation

    // Product mutations
    @JvmInline
    value class ProductsLoaded(
        val products: List<Product>
    ) : ProductsMutation

    @JvmInline
    value class LoadingError(
        val message: String
    ) : ProductsMutation

    // Search mutations
    @JvmInline
    value class SearchQueryChanged(
        val query: String
    ) : ProductsMutation

    // Sort mutations
    @JvmInline
    value class SortOptionChanged(
        val sortOption: SortOption
    ) : ProductsMutation

    // View layout mutations
    @JvmInline
    value class ViewLayoutModeChanged(
        val layoutMode: ViewLayoutPreference
    ) : ProductsMutation

    // Network mutations
    @JvmInline
    value class NetworkStatusChanged(
        val isConnected: Boolean
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

    @JvmInline
    value class SetLoadingFavorites(
        val isLoading: Boolean
    ) : ProductsMutation

    @JvmInline
    value class FavoritesLoaded(
        val products: List<Product>
    ) : ProductsMutation

    // Order flow mutations
    @JvmInline
    value class DeliveryAddressSet(
        val address: DeliveryAddress
    ) : ProductsMutation

    @JvmInline
    value class PaymentMethodSet(
        val paymentMethod: PaymentMethod
    ) : ProductsMutation

    @JvmInline
    value class OrderCreated(
        val order: Order
    ) : ProductsMutation

    @JvmInline
    value class OrderUpdated(
        val order: Order
    ) : ProductsMutation

    @JvmInline
    value class ProductRated(
        val orderId: String
    ) : ProductsMutation
}
