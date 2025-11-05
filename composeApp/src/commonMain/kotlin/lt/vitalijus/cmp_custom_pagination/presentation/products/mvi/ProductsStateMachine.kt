package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.core.mvi.StateMachine

/**
 * State Machine for managing Products feature state transitions.
 * Validates that intents are appropriate for the current state.
 *
 * Flow:
 * - LoadMore intent: Idle -> LoadingProducts (via transition)
 * - Loading completes: LoadingProducts -> Ready/Error (via onMutationComplete - async)
 * - Basket operations: Ready -> ProcessingBasket (via transition) -> Ready (via onMutationComplete)
 */
class ProductsStateMachine(
    initialState: ProductsTransitionState = ProductsTransitionState.Idle
) : StateMachine<ProductsTransitionState, ProductsIntent, ProductsMutation>(initialState) {

    override fun onTransition(intent: ProductsIntent): ProductsTransitionState {
        return when (currentState) {
            is ProductsTransitionState.Idle -> when (intent) {
                ProductsIntent.LoadMore -> ProductsTransitionState.LoadingProducts
                is ProductsIntent.NavigateTo -> ProductsTransitionState.Idle
                else -> throw IllegalStateException("Invalid transition from Idle: $intent")
            }

            is ProductsTransitionState.LoadingProducts -> when (intent) {
                // While loading, user can only navigate
                // The transition to Ready/Error happens in onMutationComplete when loading finishes
                is ProductsIntent.NavigateTo -> currentState
                else -> throw IllegalStateException("Invalid transition from LoadingProducts: $intent")
            }

            is ProductsTransitionState.Ready -> when (intent) {
                ProductsIntent.LoadMore -> ProductsTransitionState.LoadingProducts
                is ProductsIntent.AddToBasket -> ProductsTransitionState.ProcessingBasket
                is ProductsIntent.UpdateQuantity -> ProductsTransitionState.ProcessingBasket
                is ProductsIntent.RemoveProduct -> ProductsTransitionState.ProcessingBasket
                ProductsIntent.ClearBasket -> ProductsTransitionState.ProcessingBasket
                is ProductsIntent.NavigateTo -> ProductsTransitionState.Ready
            }

            is ProductsTransitionState.Error -> when (intent) {
                ProductsIntent.LoadMore -> ProductsTransitionState.LoadingProducts
                is ProductsIntent.NavigateTo -> currentState
                else -> throw IllegalStateException("Invalid transition from Error: $intent")
            }

            is ProductsTransitionState.ProcessingBasket -> when (intent) {
                // While processing basket, user can only navigate
                // The transition to Ready happens in onMutationComplete when operation finishes
                is ProductsIntent.NavigateTo -> currentState
                else -> throw IllegalStateException("Invalid transition from ProcessingBasket: $intent")
            }
        }
    }

    override fun onMutationComplete(mutation: ProductsMutation): ProductsTransitionState {
        return when (mutation) {
            is ProductsMutation.SetLoading ->
                if (mutation.isLoading) {
                    ProductsTransitionState.LoadingProducts
                } else {
                    ProductsTransitionState.Ready
                }

            is ProductsMutation.ProductsLoaded -> ProductsTransitionState.Ready

            is ProductsMutation.LoadingError -> ProductsTransitionState.Error(mutation.message)

            is ProductsMutation.BasketUpdated -> ProductsTransitionState.Ready
        }
    }
}
