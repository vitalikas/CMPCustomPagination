package lt.vitalijus.cmp_custom_pagination.data.source.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import lt.vitalijus.cmp_custom_pagination.data.model.ProductDto
import lt.vitalijus.cmp_custom_pagination.data.model.ProductResponseDto
import kotlin.coroutines.coroutineContext

class ProductApiImpl(
    private val httpClient: HttpClient
) : ProductApi {

    override suspend fun getProducts(
        page: Int,
        pageSize: Int
    ): Result<ProductResponseDto> {
        delay(1000)
        val body = try {
            val response = httpClient.get(
                "https://dummyjson.com/products"
            ) {
                contentType(ContentType.Application.Json)
                parameter("limit", pageSize)
                parameter("skip", page * pageSize)
            }
            response.body<ProductResponseDto>()
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            return Result.failure(e)
        }
        return Result.success(body)
    }

    /**
     * Fetches multiple products by their IDs in parallel using async-await pattern.
     * Makes concurrent network requests for each product ID.
     *
     * Example: For IDs [1, 5, 23, 42], makes parallel calls to:
     * - GET https://dummyjson.com/products/1
     * - GET https://dummyjson.com/products/5
     * - GET https://dummyjson.com/products/23
     * - GET https://dummyjson.com/products/42
     */
    override suspend fun getProductsByIds(ids: Set<Long>): Result<List<ProductDto>> {
        if (ids.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            // Use coroutineScope to launch parallel requests
            val products = coroutineScope {
                // Create async jobs for each product ID
                val deferredProducts = ids.map { id ->
                    async {
                        // Fetch individual product by ID
                        val response = httpClient.get(
                            "https://dummyjson.com/products/$id"
                        ) {
                            contentType(ContentType.Application.Json)
                        }
                        response.body<ProductDto>()
                    }
                }

                // Wait for all requests to complete
                deferredProducts.awaitAll()
            }

            Result.success(products)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Result.failure(e)
        }
    }
}
