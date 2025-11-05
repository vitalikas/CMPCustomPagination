package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

/**
 * High-level transition states for the Products feature state machine.
 * Tracks the complete lifecycle to validate which intents are allowed.
 *
 * Flow: Idle -> LoadingProducts -> Ready/Error -> Ready -> ProcessingBasket -> Ready
 */
sealed interface ProductsTransitionState {
    /**
     * Initial state - no products loaded yet.
     */
    data object Idle : ProductsTransitionState

    /**
     * Loading products from the API (prevents adding to basket while loading).
     */
    data object LoadingProducts : ProductsTransitionState

    /**
     * Products loaded successfully, user can interact.
     */
    data object Ready : ProductsTransitionState

    /**
     * Error occurred while loading products.
     */
    data class Error(val message: String) : ProductsTransitionState

    /**
     * Processing a basket operation (prevents concurrent basket operations).
     */
    data object ProcessingBasket : ProductsTransitionState
}
