package lt.vitalijus.cmp_custom_pagination.presentation.products.navigation

import androidx.navigation3.runtime.NavKey
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3.Route

class DefaultScreenTitleProvider : ScreenTitleProvider {

    override fun getTitleForRoute(route: NavKey?): String {
        return when (route) {
            is Route.Products -> "Products"
            is Route.ProductDetail -> "Product Details"
            is Route.Basket -> "Shopping Basket"
            is Route.Favorites -> "Favorites"
            is Route.Orders -> "My Orders"
            is Route.Settings -> "Settings"
            is Route.Delivery -> "Delivery Information"
            is Route.Payment -> "Payment"
            is Route.OrderTracking -> "Order Tracking"
            is Route.OrderRating -> "Rate Products"
            null -> ""
            else -> "Unknown"
        }
    }

    override fun getTitleForRoute(route: String?): String {
        return when {
            route == null -> ""
            route.contains("ProductList") || route.contains("Products") -> "Products"
            route.contains("Basket") -> "Shopping Basket"
            route.contains("Favorites") -> "Favorites"
            route.contains("ProductDetails") -> "Product Details"
            route.contains("Orders") -> "My Orders"
            route.contains("Settings") -> "Settings"
            route.contains("Delivery") -> "Delivery Information"
            route.contains("Payment") -> "Payment"
            route.contains("OrderTracking") -> "Order Tracking"
            route.contains("OrderRating") -> "Rate Products"
            else -> "Unknown"
        }
    }
}
