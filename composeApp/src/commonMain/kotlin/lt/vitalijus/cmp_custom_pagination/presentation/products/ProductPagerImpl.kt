package lt.vitalijus.cmp_custom_pagination.presentation.products

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.Pager
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem

class ProductPagerImpl(
    private val pager: Pager<Int, ProductItem>
) : ProductPager {

    override suspend fun loadNextProducts() {
        pager.loadNextItems()
    }

    override fun reset() {
        pager.reset()
    }
}
