package lt.vitalijus.cmp_custom_pagination.data.repository.impl

import lt.vitalijus.cmp_custom_pagination.data.mapper.toProduct
import lt.vitalijus.cmp_custom_pagination.data.source.local.InMemoryProductDataSource
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.repository.CursorProductReader

class InMemoryProductRepository(
    private val inMemoryDataSource: InMemoryProductDataSource
) : CursorProductReader {

    override suspend fun getProductsWithCursor(
        cursor: String?,
        limit: Int
    ): Result<ProductItem> {
        return try {
            val result = inMemoryDataSource.getProductsAfter(
                cursor = cursor,
                limit = limit
            )
            result.fold(
                onSuccess = { response ->
                    val products = response.products.map { it.toProduct() }
                    val productItem = ProductItem(
                        products = products,
                        total = response.total,
                        nextCursor = response.cursor
                    )
                    Result.success(productItem)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
