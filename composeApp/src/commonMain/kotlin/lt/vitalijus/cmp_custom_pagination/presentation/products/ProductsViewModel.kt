package lt.vitalijus.cmp_custom_pagination.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import lt.vitalijus.cmp_custom_pagination.domain.paging.ProductPagingFactory
import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsEffect
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsStateMachine
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsStore

/**
 * ViewModel using MVI+Redux architecture.
 * Wraps the ProductsStore for lifecycle management.
 */
class ProductsViewModel(
    pagerFactory: ProductPagingFactory,
    addToBasketUseCase: AddToBasketUseCase,
    stateMachine: ProductsStateMachine
) : ViewModel() {

    private val store = ProductsStore(
        pagerFactory = pagerFactory,
        addToBasketUseCase = addToBasketUseCase,
        stateMachine = stateMachine,
        scope = viewModelScope
    )

    val state: StateFlow<ProductsState> = store.state
    val effects: Flow<ProductsEffect> = store.effects

    fun processIntent(intent: ProductsIntent) {
        store.processIntent(intent)
    }
}
