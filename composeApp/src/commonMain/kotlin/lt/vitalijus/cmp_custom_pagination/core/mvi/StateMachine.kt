package lt.vitalijus.cmp_custom_pagination.core.mvi

/**
 * Reusable generic state machine for managing state transitions.
 *
 * @param S Transition state type (e.g., ProductsTransitionState)
 * @param I Intent type (e.g., ProductsIntent)
 * @param M Mutation type (e.g., ProductsMutation) - optional for async updates
 *
 * Usage example:
 * ```
 * class ProductsStateMachine(
 *     initialState: ProductsTransitionState = ProductsTransitionState.Idle
 * ) : StateMachine<ProductsTransitionState, ProductsIntent, ProductsMutation>(initialState) {
 *     override fun onTransition(intent: ProductsIntent): ProductsTransitionState {
 *         return when (currentState) {
 *             // Define synchronous state transitions
 *         }
 *     }
 *
 *     override fun onMutationComplete(mutation: ProductsMutation): ProductsTransitionState {
 *         return when (mutation) {
 *             // Define async state updates
 *         }
 *     }
 * }
 * ```
 */
abstract class StateMachine<S, I, M>(
    initialState: S
) {

    var currentState: S = initialState
        protected set

    /**
     * Validates and performs state transition based on intent.
     * @throws IllegalStateException if transition is invalid
     * @return The new state after transition
     */
    fun transition(intent: I): S {
        currentState = onTransition(intent = intent)
        return currentState
    }

    /**
     * Override this to define state transitions for intents.
     * This should be synchronous and throw IllegalStateException for invalid transitions.
     */
    protected abstract fun onTransition(intent: I): S

    /**
     * Updates state based on async mutation results (optional).
     * Override this if your state machine needs to react to async operations.
     * @throws NotImplementedError if not overridden
     */
    open fun onMutationComplete(mutation: M): S {
        throw NotImplementedError(
            "onMutationComplete is not implemented. Override this method if you need mutation-based state updates."
        )
    }

    /**
     * Convenience method to update current state from mutation and return it.
     */
    fun applyMutation(mutation: M): S {
        currentState = onMutationComplete(mutation)
        return currentState
    }

    /**
     * Check if a transition is valid without actually performing it.
     * Useful for UI state (enabling/disabling buttons).
     */
    fun isTransitionValid(intent: I): Boolean {
        return try {
            val savedState = currentState
            onTransition(intent)
            currentState = savedState // Restore state after check
            true
        } catch (_: IllegalStateException) {
            false
        }
    }
}
