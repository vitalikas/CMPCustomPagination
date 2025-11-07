package lt.vitalijus.cmp_custom_pagination.presentation.products

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    val route: String

    @Serializable
    data object Basket : Screen {
        override val route: String = "Basket"
    }

    @Serializable
    data object ProductList : Screen {
        override val route: String = "Products"
    }

    @Serializable
    data object Favorites : Screen {
        override val route: String = "Favorites"
    }

    @Serializable
    data class ProductDetails(val productId: Long) : Screen {
        override val route: String = "ProductDetails/$productId"
    }
}
