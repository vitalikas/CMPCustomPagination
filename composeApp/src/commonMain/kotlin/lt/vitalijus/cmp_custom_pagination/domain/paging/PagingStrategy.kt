package lt.vitalijus.cmp_custom_pagination.domain.paging

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager

/**
 * Strategy for different pagination approaches (offset, cursor, etc.)
 * Directly creates configured ProductPager instances
 */
interface PagingStrategy {

    fun createProductPager(onEvent: (PagingEvent) -> Unit): ProductPager
}
