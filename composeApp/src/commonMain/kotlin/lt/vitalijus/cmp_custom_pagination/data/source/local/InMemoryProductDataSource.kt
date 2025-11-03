package lt.vitalijus.cmp_custom_pagination.data.source.local

import kotlinx.coroutines.delay
import lt.vitalijus.cmp_custom_pagination.data.model.ProductDto
import kotlin.random.Random

// Custom response for cursor-based pagination
data class CursorResponse(
    val products: List<ProductDto>,
    val total: Long,
    val cursor: String? = null // Opaque cursor token for the next page
)

class InMemoryProductDataSource {

    private val dummyProducts = generateDummyProducts()

    suspend fun getProductsAfter(
        cursor: String? = null,
        limit: Int
    ): Result<CursorResponse> {
        delay(1000) // Simulate network delay

        return try {
            // Validate limit
            require(limit > 0) { "Limit must be positive" }

            // Filter products based on cursor (get products with ID > cursor)
            val filteredProducts = if (cursor == null) {
                dummyProducts
            } else {
                val cursorId = cursor.toLongOrNull()
                    ?: return Result.failure(IllegalArgumentException("Invalid cursor format: $cursor"))

                val productsAfterCursor = dummyProducts.filter { it.id > cursorId }

                if (dummyProducts.none { it.id == cursorId }) {
                    println("Cursor product ID=$cursorId not found (may have been deleted), continuing pagination normally")
                }

                productsAfterCursor
            }

            // Take only the requested limit
            val products = filteredProducts.take(limit)

            // Create next cursor token if there are more products available
            val nextCursor = if (filteredProducts.size > limit) {
                "${products.last().id}" // ID of the last item in current page
            } else null

            println("DataSource: cursor='$cursor', limit=$limit, returned ${products.size} products, nextCursor='$nextCursor'")

            val response = CursorResponse(
                products = products,
                total = dummyProducts.size.toLong(),
                cursor = nextCursor
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateDummyProducts(): List<ProductDto> {
        val categories = listOf("Electronics", "Clothing", "Home", "Books", "Sports", "Beauty")
        val brands = listOf("Apple", "Samsung", "Nike", "Adidas", "Sony", "LG", "Generic")
        val adjectives =
            listOf("Premium", "Budget", "Professional", "Compact", "Wireless", "Smart", "Classic")
        val productTypes = listOf(
            "Phone",
            "Laptop",
            "Shirt",
            "Shoes",
            "Headphones",
            "Watch",
            "Camera",
            "Book",
            "Backpack",
            "Sunglasses"
        )

        return (1..100).map { id ->
            val category = categories.random()
            val brand = brands.random()
            val adjective = adjectives.random()
            val productType = productTypes.random()

            ProductDto(
                id = id.toLong(),
                title = "$adjective $brand $productType",
                description = "High-quality $adjective $productType from $brand. Perfect for everyday use with excellent durability and performance.",
                price = Random.nextDouble(10.0, 999.99).let { (it * 100).toInt() / 100.0 },
                category = category,
                brand = brand,
                thumbnail = "https://picsum.photos/200/200?random=$id"
            )
        }
    }
}
