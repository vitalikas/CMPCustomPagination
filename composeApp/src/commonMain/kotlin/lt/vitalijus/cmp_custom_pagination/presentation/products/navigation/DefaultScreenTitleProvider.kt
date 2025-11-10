package lt.vitalijus.cmp_custom_pagination.presentation.products.navigation

class DefaultScreenTitleProvider : ScreenTitleProvider {

    override fun getTitleForRoute(route: String?): String {
        return when {
            route?.contains("ProductList") == true -> "Product List"
            route?.contains("Basket") == true -> "Shopping Basket"
            route?.contains("Favorites") == true -> "Favorites"
            route?.contains("ProductDetails") == true -> "Product Details"
            route?.contains("Orders") == true -> "My Orders"
            route?.contains("Delivery") == true -> "Delivery Information"
            route?.contains("Payment") == true -> "Payment"
            route?.contains("OrderTracking") == true -> "Order Tracking"
            route?.contains("OrderRating") == true -> "Rate Products"
            else -> "Unknown route"
        }
    }
}
