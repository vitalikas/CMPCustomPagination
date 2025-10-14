package lt.vitalijus.cmp_custom_pagination.domain.paging

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.Pager
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingProvider
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingStateHandler
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.repository.OffsetBasedProductReader

class OffsetBasedPagingStrategy(
    private val repository: OffsetBasedProductReader
) : PagingStrategy {

    override fun createProductPager(onEvent: (PagingEvent) -> Unit): ProductPager {
        val config = OffsetBasedConfig()

        val provider = object : PagingProvider<Int, ProductItem> {
            override suspend fun loadPage(key: Int): Result<ProductItem> {
                return repository.getProducts(
                    page = key,
                    pageSize = config.pageSize
                )
            }

            override suspend fun getNextKey(
                currentKey: Int,
                result: ProductItem
            ): Int {
                return currentKey + 1
            }

            override fun isEndReached(
                currentKey: Int,
                result: ProductItem
            ): Boolean {
                return (currentKey * config.pageSize) >= result.total
            }
        }

        val stateHandler = object : PagingStateHandler<ProductItem> {
            override fun onLoadingStateChanged(isLoading: Boolean) {
                onEvent(PagingEvent.LoadingChanged(isLoading = isLoading))
            }

            override suspend fun onSuccess(result: ProductItem) {
                onEvent(PagingEvent.ProductsLoaded(products = result.products))
            }

            override suspend fun onError(error: Throwable?) {
                onEvent(PagingEvent.Error(message = error?.message))
            }
        }

        val pager = Pager(
            pagingConfig = config,
            pagingProvider = provider,
            pagingStateHandler = stateHandler
        )

        return ProductPagerImpl<Int>(pager = pager)
    }
}
