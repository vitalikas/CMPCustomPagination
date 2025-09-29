package lt.vitalijus.cmp_custom_pagination.domain.navigation

import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

interface Navigator {

    fun navigateToScreen(screen: Screen)
}
