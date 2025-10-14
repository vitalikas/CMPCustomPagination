package lt.vitalijus.cmp_custom_pagination.domain.paging

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.Pager
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingProvider
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingStateHandler
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.repository.CursorProductReader

class CursorBasedPagingStrategy(
    private val cursorReader: CursorProductReader
) : PagingStrategy {

    override fun createProductPager(onEvent: (PagingEvent) -> Unit): ProductPager {
        val config = CursorBasedConfig()

        val provider = object : PagingProvider<String?, ProductItem> {
            override suspend fun loadPage(key: String?): Result<ProductItem> {
                // key is the cursor (null for first page, "20" for subsequent pages)
                val cursor = if (key.isNullOrEmpty()) null else key
                println("Loading page with current cursor: $cursor")
                return cursorReader.getProductsWithCursor(
                    cursor = cursor,
                    limit = config.pageSize
                )
            }

            override suspend fun getNextKey(
                currentKey: String?,
                result: ProductItem
            ): String? {
                // Return the nextCursor from the result
                return result.nextCursor
            }

            override fun isEndReached(
                currentKey: String?,
                result: ProductItem
            ): Boolean {
                // End reached when there's no nextCursor or no products returned
                return result.nextCursor == null || result.products.isEmpty()
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

        return ProductPagerImpl(pager = pager)
    }
}
