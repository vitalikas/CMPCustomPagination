package lt.vitalijus.cmp_custom_pagination.domain

import lt.vitalijus.cmp_custom_pagination.domain.model.Product

sealed interface PagingEvent {

    data class LoadingChanged(val isLoading: Boolean) : PagingEvent
    data class ProductsLoaded(val products: List<Product>) : PagingEvent
    data class Error(val message: String?) : PagingEvent
}