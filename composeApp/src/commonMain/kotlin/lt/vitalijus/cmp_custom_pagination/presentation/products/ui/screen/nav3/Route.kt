package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Products : Route

    @Serializable
    data object Basket : Route

    @Serializable
    data class ProductDetail(val productId: Long) : Route

    @Serializable
    data object Favorites : Route

    @Serializable
    data object Orders : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Delivery : Route

    @Serializable
    data object Payment : Route

    @Serializable
    data class OrderTracking(val orderId: String) : Route

    @Serializable
    data class OrderRating(val orderId: String) : Route
}
