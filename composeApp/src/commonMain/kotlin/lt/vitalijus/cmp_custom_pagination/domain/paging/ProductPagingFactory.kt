package lt.vitalijus.cmp_custom_pagination.domain.paging

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager

/**
 * Factory that delegates to PagingStrategy for creating ProductPager instances.
 */
class ProductPagingFactory(
    private val pagingStrategy: PagingStrategy
) {

    fun create(onEvent: (PagingEvent) -> Unit): ProductPager {
        return pagingStrategy.createProductPager(onEvent = onEvent)
    }
}
