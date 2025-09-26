package lt.vitalijus.cmp_custom_pagination.data.repository.impl

import lt.vitalijus.cmp_custom_pagination.data.mapper.toProductItem
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productApi: ProductApi
) : ProductRepository {

    override suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Result<ProductItem> {
        return productApi.getProducts(page, pageSize).map { productResponseDto ->
            productResponseDto.toProductItem()
        }
    }
}
