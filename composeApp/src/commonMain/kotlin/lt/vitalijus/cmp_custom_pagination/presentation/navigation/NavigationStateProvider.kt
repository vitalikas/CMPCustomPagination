package lt.vitalijus.cmp_custom_pagination.presentation.navigation

import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

interface NavigationStateProvider {

    fun getCurrentRoute(): String?
    fun getCurrentScreen(): Screen?
}
