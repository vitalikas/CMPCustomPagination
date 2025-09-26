package lt.vitalijus.cmp_custom_pagination.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    val products: List<ProductDto>,
    val total: Long
)

@Serializable
data class ProductDto(
    val id: Long,
    val title: String,
    val price: Double,
    val description: String? = null,
    val category: String? = null,
    val brand: String? = null,
    val thumbnail: String? = null
) {
    // simulating wholesale cost
    val costPrice: Double
        get() = price * 0.7

    val retailPrice: Double
        get() = price
}
