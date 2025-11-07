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
    val thumbnail: String? = null,
    val rating: Double? = null,
    val stock: Int? = null,
    val tags: List<String>? = null,
    val sku: String? = null,
    val weight: Int? = null,
    val dimensions: DimensionsDto? = null,
    val warrantyInformation: String? = null,
    val shippingInformation: String? = null,
    val availabilityStatus: String? = null,
    val reviews: List<ReviewDto>? = null,
    val returnPolicy: String? = null,
    val minimumOrderQuantity: Int? = null,
    val images: List<String>? = null,
    val discountPercentage: Double? = null
) {
    // simulating wholesale cost
    val costPrice: Double
        get() = price * 0.7

    val retailPrice: Double
        get() = price
}

@Serializable
data class DimensionsDto(
    val width: Double? = null,
    val height: Double? = null,
    val depth: Double? = null
)

@Serializable
data class ReviewDto(
    val rating: Int,
    val comment: String,
    val date: String,
    val reviewerName: String,
    val reviewerEmail: String
)
