package lt.vitalijus.cmp_custom_pagination.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.paging.PagingEvent
import lt.vitalijus.cmp_custom_pagination.domain.paging.ProductPagingFactory
import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase

class ProductsViewModel(
    pagerFactory: ProductPagingFactory,
    private val addToBasketUseCase: AddToBasketUseCase
) : ViewModel() {

    private val _browseProductsState = MutableStateFlow(BrowseProductsState())
    private val _basketState = MutableStateFlow(BasketState())

    val state = combine(
        _browseProductsState,
        _basketState
    ) { browseProductsState, basketState ->
        ProductsState(
            browseProductsState = browseProductsState,
            basketState = basketState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        ProductsState()
    )

    private val pager = pagerFactory.create { event ->
        when (event) {
            is PagingEvent.LoadingChanged -> {
                _browseProductsState.update {
                    it.copy(isLoadingMore = event.isLoading)
                }
            }

            is PagingEvent.ProductsLoaded -> {
                _browseProductsState.update {
                    it.copy(
                        products = it.products + event.products,
                        error = null
                    )
                }
            }

            is PagingEvent.Error -> {
                _browseProductsState.update {
                    it.copy(error = event.message)
                }
            }
        }
    }

    fun loadNextProducts() {
        viewModelScope.launch {
            pager.loadNextProducts()
        }
    }

    fun addToBasket(product: Product, quantity: Int) {
        _basketState.update { currentState ->
            val result = addToBasketUseCase.execute(
                currentItems = currentState.items,
                product = product,
                quantity = quantity
            )

            result.fold(
                onSuccess = { updatedItems ->
                    currentState.copy(items = updatedItems)
                },
                onFailure = {
                    // In a real app, you'd handle this error properly
                    // For now, return unchanged state
                    currentState
                }
            )
        }
    }

    fun removeFromBasket(productId: Long) {
        _basketState.update { currentState ->
            currentState.copy(items = currentState.items.filter { it.product.id != productId })
        }
    }

    fun clearBasket() {
        _basketState.update { BasketState() }
    }

    fun updateQuantity(productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromBasket(productId)
            return
        }

        _basketState.update { currentState ->
            val updatedItems = currentState.items.map {
                if (it.product.id == productId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
            currentState.copy(items = updatedItems)
        }
    }
}
