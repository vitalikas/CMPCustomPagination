package lt.vitalijus.cmp_custom_pagination.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Room entity for caching products from the Products screen.
 * Implements offline-first strategy with timestamp-based invalidation.
 */
@Entity(tableName = "cached_products")
data class CachedProductEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val brand: String?,
    val thumbnail: String?,
    val cachedAt: Long, // Timestamp in milliseconds when cached
    val page: Int = 0 // Which page this product belongs to (for pagination tracking)
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
            thumbnail = thumbnail
        )
    }

    companion object {
        /**
         * Create entity from domain model
         */
        fun fromDomain(
            product: Product,
            cachedAt: Long,
            page: Int = 0
        ): CachedProductEntity {
            return CachedProductEntity(
                id = product.id,
                title = product.title,
                description = product.description,
                price = product.price,
                category = product.category,
                brand = product.brand,
                thumbnail = product.thumbnail,
                cachedAt = cachedAt,
                page = page
            )
        }
    }
}
