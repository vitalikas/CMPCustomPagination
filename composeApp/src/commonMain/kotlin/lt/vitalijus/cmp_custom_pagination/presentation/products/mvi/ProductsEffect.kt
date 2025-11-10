package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

sealed interface ProductsEffect {

    data class ShowError(val message: String) : ProductsEffect
    data object ShowBasketUpdated : ProductsEffect
    data class ShowFavoriteToggled(val isAdded: Boolean) : ProductsEffect

    // Navigation effects
    data class NavigateTo(val screen: Screen) : ProductsEffect
    data object NavigateBack : ProductsEffect

    // Order effects
    data class OrderCreated(val orderId: String) : ProductsEffect
    data class OrderDelivered(val orderId: String) : ProductsEffect
}
