package lt.vitalijus.cmp_custom_pagination.data.source.remote.api

import lt.vitalijus.cmp_custom_pagination.data.model.ProductResponseDto

interface ProductApi {

    suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Result<ProductResponseDto>
}
