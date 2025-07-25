package lt.vitalijus.cmp_custom_pagination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val api: ProductsApi
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state = _state
        .onStart {
            loadNextProducts()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            ProductsState()
        )

    private val pager = Pager<Int, ProductResponseDto>(
        initialKey = Pager.INITIAL_PAGE_KEY,
        onLoadUpdated = { isLoading ->
            _state.update {
                it.copy(isLoadingMore = isLoading)
            }
        },
        onRequest = { currentPage ->
            api.getProducts(
                page = currentPage,
                pageSize = Pager.PAGE_SIZE
            )
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { t ->
            _state.update {
                it.copy(error = t?.message)
            }
        },
        onSuccess = { response, _ ->
            _state.update {
                it.copy(
                    products = it.products + response.products,
                    error = null
                )
            }
        },
        endReached = { currentPage, response ->
            (currentPage * Pager.PAGE_SIZE) >= response.total
        }
    )

    fun loadNextProducts() {
        viewModelScope.launch {
            pager.loadNextItems()
        }
    }
}
