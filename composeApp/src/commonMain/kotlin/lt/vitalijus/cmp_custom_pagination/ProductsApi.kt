package lt.vitalijus.cmp_custom_pagination

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class ProductsApi(
    private val httpClient: HttpClient
) {

    suspend fun getProducts(
        page: Int = 0,
        pageSize: Int = 10
    ): Result<ProductResponseDto> {
        delay(2000)
        val body = try {
            val response = httpClient.get(
                "https://dummyjson.com/products?select=title,price"
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
