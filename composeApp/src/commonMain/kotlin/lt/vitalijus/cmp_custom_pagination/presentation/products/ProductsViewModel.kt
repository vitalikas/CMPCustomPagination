package lt.vitalijus.cmp_custom_pagination.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.PagingEvent
import lt.vitalijus.cmp_custom_pagination.domain.ProductPagerFactory
import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

class ProductsViewModel(
    pagerFactory: ProductPagerFactory
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

    private val pager: ProductPager = pagerFactory.create { event ->
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
            val existingItem = currentState.items.find { it.product.id == product.id }

            if (existingItem != null) {
                val updatedItems = currentState.items.map {
                    if (it.product.id == product.id) {
                        it.copy(quantity = it.quantity + quantity)
                    } else {
                        it
                    }
                }
                currentState.copy(items = updatedItems)
            } else {
                val newItem = BasketItem(product = product, quantity = quantity)
                currentState.copy(items = currentState.items + newItem)
            }
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
