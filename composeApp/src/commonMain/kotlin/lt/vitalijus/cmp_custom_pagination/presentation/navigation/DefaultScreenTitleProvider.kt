package lt.vitalijus.cmp_custom_pagination.presentation.navigation

class DefaultScreenTitleProvider : ScreenTitleProvider {

    override fun getTitleForRoute(route: String?): String {
        return when {
            route?.contains("ProductList") == true -> "Product List"
            route?.contains("Basket") == true -> "Shopping Basket"
            else -> "Unknown route"
        }
    }
}
