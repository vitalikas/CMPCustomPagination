package lt.vitalijus.cmp_custom_pagination.presentation.products

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    val route: String
    val iconText: String

    @Serializable
    data object Basket : Screen {
        override val route: String = "Basket"
        override val iconText: String = "🛒"
    }

    @Serializable
    data object ProductList : Screen {
        override val route: String = "Products"
        override val iconText: String = "📋"
    }
}
