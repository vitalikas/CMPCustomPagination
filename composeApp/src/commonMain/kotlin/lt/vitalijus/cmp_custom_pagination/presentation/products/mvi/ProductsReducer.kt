package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

/**
 * Pure reducer function: (State, Mutation) -> State
 *
 * No side effects allowed here - must be pure and synchronous.
 * The mutation type itself tells us what state changes to make.
 */
object ProductsReducer {

    fun reduce(
        state: ProductsState,
        mutation: ProductsMutation
    ): ProductsState {
        return when (mutation) {
            is ProductsMutation.SetLoading -> {
                state.copy(
                    isLoadingMore = mutation.isLoading,
                    error = if (mutation.isLoading) null else state.error
                )
            }

            is ProductsMutation.ProductsLoaded -> {
                state.copy(
                    products = state.products + mutation.products,
                    isLoadingMore = false,
                    error = null
                )
            }

            is ProductsMutation.LoadingError -> {
                state.copy(
                    isLoadingMore = false,
                    error = mutation.message
                )
            }

            is ProductsMutation.BasketUpdated -> {
                state.copy(
                    basketItems = mutation.items,
                    isLoadingMore = false,
                    error = null
                )
            }
        }
    }
}
