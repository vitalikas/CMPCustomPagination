package lt.vitalijus.cmp_custom_pagination.presentation.products

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.DefaultPagingConfiguration
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.Pager
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingStateHandler
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingStrategy
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.PagingEvent
import lt.vitalijus.cmp_custom_pagination.domain.ProductPagerFactory
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.usecase.products.LoadProductsUseCase

class ProductPagerFactoryImpl(
    private val loadProductsUseCase: LoadProductsUseCase
) : ProductPagerFactory {

    override fun create(onEvent: (PagingEvent) -> Unit): ProductPager {
        val pagingConfig = DefaultPagingConfiguration(
            pageSize = 10,
            initialKey = 1,
            enableRetry = true,
            maxRetries = 2
        )

        val productPagingStrategy = object : PagingStrategy<Int, ProductItem> {
            override suspend fun loadPage(key: Int): Result<ProductItem> {
                return loadProductsUseCase.execute(
                    page = key,
                    pageSize = pagingConfig.pageSize
                )
            }

            override suspend fun getNextKey(currentKey: Int, result: ProductItem): Int {
                return currentKey + 1
            }

            override fun isEndReached(currentKey: Int, result: ProductItem): Boolean {
                return (currentKey * pagingConfig.pageSize) >= result.total
            }
        }

        val productStateHandler = object : PagingStateHandler<ProductItem> {
            override fun onLoadingStateChanged(isLoading: Boolean) {
                onEvent(PagingEvent.LoadingChanged(isLoading))
            }

            override suspend fun onSuccess(result: ProductItem) {
                onEvent(PagingEvent.ProductsLoaded(result.products))
            }

            override suspend fun onError(error: Throwable?) {
                onEvent(PagingEvent.Error(error?.message))
            }
        }

        val pager = Pager(
            config = pagingConfig,
            strategy = productPagingStrategy,
            stateHandler = productStateHandler
        )

        return ProductPagerImpl(pager = pager)
    }
}
