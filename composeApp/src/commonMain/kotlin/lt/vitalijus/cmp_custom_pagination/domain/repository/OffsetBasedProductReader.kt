package lt.vitalijus.cmp_custom_pagination.domain.repository

import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem

interface OffsetBasedProductReader {

    suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Result<ProductItem>
}
