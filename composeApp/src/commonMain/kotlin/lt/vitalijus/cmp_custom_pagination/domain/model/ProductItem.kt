package lt.vitalijus.cmp_custom_pagination.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
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
    val dimensions: Dimensions? = null,
    val warrantyInformation: String? = null,
    val shippingInformation: String? = null,
    val availabilityStatus: String? = null,
    val reviews: List<Review>? = null,
    val returnPolicy: String? = null,
    val minimumOrderQuantity: Int? = null,
    val images: List<String>? = null,
    val discountPercentage: Double? = null
) {
    val costPrice: Double
        get() = price * 0.7

    val retailPrice: Double
        get() = price
}

@Serializable
data class Dimensions(
    val width: Double? = null,
    val height: Double? = null,
    val depth: Double? = null
)

@Serializable
data class Review(
    val rating: Int,
    val comment: String,
    val date: String,
    val reviewerName: String,
    val reviewerEmail: String
)

@Serializable
data class ProductItem(
    val products: List<Product>,
    val total: Long,
    val nextCursor: String? = null
)
