package lt.vitalijus.cmp_custom_pagination.data.repository.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lt.vitalijus.cmp_custom_pagination.data.mapper.toProductItem
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.repository.OffsetBasedProductReader

class OffsetBasedProductRepository(
    private val productApi: ProductApi
) : OffsetBasedProductReader {

    override suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Result<ProductItem> {
        return withContext(Dispatchers.Default) {
            productApi.getProducts(page, pageSize).map { productResponseDto ->
                productResponseDto.toProductItem()
            }
        }
    }
}
