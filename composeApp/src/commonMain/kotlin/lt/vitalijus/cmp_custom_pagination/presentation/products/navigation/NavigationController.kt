package lt.vitalijus.cmp_custom_pagination.presentation.products.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

class NavigationController(
    private val navController: NavHostController
) : NavigationManager {

    override fun navigateToScreen(screen: Screen) {
        val currentRoute = getCurrentRoute()
        if (currentRoute?.contains(screen::class.simpleName ?: "") == true) {
            return
        }

        navController.navigate(screen) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun getCurrentRoute(): String? {
        return navController.currentBackStackEntry?.destination?.route
    }

    override fun getCurrentScreen(): Screen? {
        val route = getCurrentRoute()
        return when {
            route?.contains("ProductDetails") == true -> null
            route?.contains("ProductList") == true -> Screen.ProductList
            route?.contains("Basket") == true -> Screen.Basket
            route?.contains("Favorites") == true -> Screen.Favorites
            else -> Screen.ProductList
        }
    }
}
