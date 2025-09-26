package lt.vitalijus.cmp_custom_pagination.presentation.products

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
    val title: String,
    val iconText: String
) {
    @Serializable
    data object Basket : Screen("Basket", "🛒")

    @Serializable
    data object ProductList : Screen("Products", "📋")
}
