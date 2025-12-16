package lt.vitalijus.cmp_custom_pagination.presentation.products.navigation

import androidx.navigation3.runtime.NavKey

interface ScreenTitleProvider {

    fun getTitleForRoute(route: NavKey?): String

    fun getTitleForRoute(route: String?): String
}
