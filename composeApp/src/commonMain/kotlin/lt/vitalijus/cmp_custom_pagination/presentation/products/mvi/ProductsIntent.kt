package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.OrderStatus
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.SortOption
import lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

sealed interface ProductsIntent {

    data object LoadMore : ProductsIntent
    data object LoadAllItems : ProductsIntent  // Load all remaining items (for sorting)
    data object ManualRefresh : ProductsIntent  // User-initiated refresh (pull-to-refresh)
    data class LoadFavorites(val favoriteIds: Set<Long>) :
        ProductsIntent  // Load specific products by IDs
    data class SearchProducts(val query: String) : ProductsIntent
    data class SetSortOption(val sortOption: SortOption) : ProductsIntent
    data class SetViewLayoutMode(val layoutMode: ViewLayoutPreference) : ProductsIntent
    data class SetShowSyncTimestamp(val show: Boolean) : ProductsIntent
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
    data class ToggleFavorite(val productId: Long) : ProductsIntent

    // Order flow intents
    data class SetDeliveryAddress(val address: DeliveryAddress) : ProductsIntent
    data class SetPaymentMethod(val paymentMethod: PaymentMethod) : ProductsIntent
    data object ConfirmOrder : ProductsIntent
    data class UpdateOrderStatus(val orderId: String, val status: OrderStatus) : ProductsIntent
    data class RateProduct(
        val orderId: String,
        val productId: Long,
        val rating: Int,
        val comment: String
    ) : ProductsIntent
}
