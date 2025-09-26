package lt.vitalijus.cmp_custom_pagination.domain.repository

import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem

interface ProductReader {
    suspend fun getProducts(page: Int, pageSize: Int): Result<ProductItem>
}

interface ProductRepository : ProductReader
