package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3

import androidx.navigation3.runtime.NavKey

class Navigator(val navigationState: NavigationState) {

    fun navigate(route: NavKey) {
        if (route in navigationState.backStacks.keys) {
            navigationState.topLevelRoute = route
        } else {
            navigationState.backStacks[navigationState.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentBackStack = navigationState.backStacks[navigationState.topLevelRoute]
            ?: error("No back stack for the current top-level route")
        val currentRoute = currentBackStack.last()
        if (currentRoute == navigationState.topLevelRoute) {
            navigationState.topLevelRoute = navigationState.startRoute
        } else {
            currentBackStack.removeLastOrNull()
        }
    }

    /**
     * Clears the backstack for a specific route, keeping only the root item
     */
    fun clearBackstack(route: NavKey) {
        val backstack = navigationState.backStacks[route] ?: return
        // Remove all items except the root (first item)
        while (backstack.size > 1) {
            backstack.removeLastOrNull()
        }
    }
}
