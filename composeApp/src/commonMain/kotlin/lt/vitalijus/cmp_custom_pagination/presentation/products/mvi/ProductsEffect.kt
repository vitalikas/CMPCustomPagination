package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

sealed interface ProductsEffect {

    data class ShowError(val message: String) : ProductsEffect
    data object ShowBasketUpdated : ProductsEffect

    // Navigation effects
    data class NavigateTo(val screen: Screen) : ProductsEffect
    data object NavigateBack : ProductsEffect
}
