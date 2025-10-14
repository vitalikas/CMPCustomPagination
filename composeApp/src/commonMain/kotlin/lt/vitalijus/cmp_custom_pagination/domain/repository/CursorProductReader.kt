package lt.vitalijus.cmp_custom_pagination.domain.repository

import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem

interface CursorProductReader {

    suspend fun getProductsWithCursor(
        cursor: String?,
        limit: Int
    ): Result<ProductItem>
}
