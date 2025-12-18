package lt.vitalijus.cmp_custom_pagination.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.Review

/**
 * Room entity for caching favorite products locally.
 * Stores product data to enable offline access and faster loading.
 */
@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val brand: String?,
    val thumbnail: String?,
    val rating: Double?, // Product rating
    val images: List<String>?, // Image URLs array
    val reviews: List<Review>?, // Customer reviews
    val cachedAt: Long // Timestamp in milliseconds when cached
) {
    /**
     * Convert entity to domain model
     */
    fun toDomain(): Product {
        return Product(
            id = id,
            title = title,
            description = description,
            price = price,
            category = category,
            brand = brand,
            thumbnail = thumbnail,
            rating = rating,
            images = images,
            reviews = reviews
        )
    }

    companion object {
        /**
         * Create entity from domain model
         */
        fun fromDomain(product: Product, cachedAt: Long): FavoriteProductEntity {
            return FavoriteProductEntity(
                id = product.id,
                title = product.title,
                description = product.description,
                price = product.price,
                category = product.category,
                brand = product.brand,
                thumbnail = product.thumbnail,
                rating = product.rating,
                images = product.images,
                reviews = product.reviews,
                cachedAt = cachedAt
            )
        }
    }
}
