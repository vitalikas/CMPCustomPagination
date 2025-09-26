package lt.vitalijus.cmp_custom_pagination.data.source.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
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
}
