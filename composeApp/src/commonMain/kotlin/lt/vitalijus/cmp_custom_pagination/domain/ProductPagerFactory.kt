package lt.vitalijus.cmp_custom_pagination.domain

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager

fun interface ProductPagerFactory {

    fun create(onEvent: (PagingEvent) -> Unit): ProductPager
}
