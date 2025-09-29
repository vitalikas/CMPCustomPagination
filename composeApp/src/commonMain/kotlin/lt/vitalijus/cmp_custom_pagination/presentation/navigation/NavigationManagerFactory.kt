package lt.vitalijus.cmp_custom_pagination.presentation.navigation

import androidx.navigation.NavHostController

fun interface NavigationManagerFactory {

    fun create(navController: NavHostController): NavigationManager
}
