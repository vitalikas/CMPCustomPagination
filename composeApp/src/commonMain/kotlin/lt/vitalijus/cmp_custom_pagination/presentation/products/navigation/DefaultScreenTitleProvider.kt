package lt.vitalijus.cmp_custom_pagination.presentation.products.navigation

class DefaultScreenTitleProvider : ScreenTitleProvider {

    override fun getTitleForRoute(route: String?): String {
        return when {
            route?.contains("ProductList") == true -> "Product List"
            route?.contains("Basket") == true -> "Shopping Basket"
            route?.contains("Settings") == true -> "Settings"
            else -> "Unknown route"
        }
    }
}
