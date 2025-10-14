package lt.vitalijus.cmp_custom_pagination.domain.usecase.products

import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.repository.OffsetBasedProductReader

/**
 * Use case for loading products with business rules
 * Single responsibility: Product loading business logic
 */
class LoadProductsUseCase(
    private val offsetBasedProductReader: OffsetBasedProductReader
) {

    suspend fun execute(page: Int, pageSize: Int): Result<ProductItem> {
        require(page >= 1) { "Page must be >= 1" }
        require(pageSize > 0) { "Page size must be > 0" }
        require(pageSize <= MAX_PAGE_SIZE) { "Page size cannot exceed $MAX_PAGE_SIZE" }

        return offsetBasedProductReader.getProducts(page, pageSize)
    }

    companion object {
        private const val MAX_PAGE_SIZE = 100
    }
}
