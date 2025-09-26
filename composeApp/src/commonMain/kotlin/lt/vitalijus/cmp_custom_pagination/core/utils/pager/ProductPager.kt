package lt.vitalijus.cmp_custom_pagination.core.utils.pager

interface ProductPager {

    suspend fun loadNextProducts()
    fun reset()
}
