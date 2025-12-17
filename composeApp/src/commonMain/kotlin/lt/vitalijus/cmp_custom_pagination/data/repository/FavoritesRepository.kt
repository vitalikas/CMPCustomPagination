package lt.vitalijus.cmp_custom_pagination.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.entity.FavoriteProductEntity
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Repository for managing favorite products with reactive Flow.
 *
 * Strategy:
 * 1. Expose a Flow that automatically updates when database changes
 * 2. Fetch from network in background and update cache
 * 3. UI automatically reacts to database updates via Flow
 */
class FavoritesRepository(
    private val favoriteProductDao: FavoriteProductDao,
    private val productApi: ProductApi
) {

    companion object {
        private const val CACHE_EXPIRATION_MS = 3_600_000L // 1 hour
    }

    /**
     * Observe cached favorites - automatically updates when database changes!
     * This is the reactive way to get favorites.
     */
    fun observeFavorites(ids: Set<Long>): Flow<List<Product>> {
        if (ids.isEmpty()) {
            return flowOf(emptyList())
        }

        return favoriteProductDao.observeProductsByIds(ids)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    /**
     * Get cached favorites (instant, no network call)
     */
    suspend fun getCachedFavorites(ids: Set<Long>): Result<List<Product>> {
        if (ids.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            val cached = favoriteProductDao.getProductsByIds(ids = ids)
            val products = cached.map { it.toDomain() }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if cache needs refresh
     */
    suspend fun shouldRefresh(ids: Set<Long>): Boolean {
        if (ids.isEmpty()) return false

        return try {
            val cached = favoriteProductDao.getProductsByIds(ids = ids)

            // Refresh if:
            // 1. No cached data
            // 2. Not all products are cached
            // 3. Any product is expired
            cached.isEmpty() ||
                    cached.size != ids.size ||
                    cached.any { isCacheExpired(cachedAt = it.cachedAt) }
        } catch (e: Exception) {
            true // On error, try to refresh
        }
    }

    /**
     * Fetch fresh data from network and update cache
     */
    suspend fun refreshFavorites(ids: Set<Long>): Result<List<Product>> {
        if (ids.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            // Fetch from API (parallel network calls)
            productApi.getProductsByIds(ids = ids)
                .map { productDtos -> // ✅ Executes ONLY on success, transforms T -> U
                    val currentTime = currentTimeMillis()

                    // Convert to domain models
                    val products = productDtos.map { dto ->
                        Product(
                            id = dto.id,
                            title = dto.title,
                            description = dto.description,
                            price = dto.price,
                            category = dto.category,
                            brand = dto.brand,
                            thumbnail = dto.thumbnail
                        )
                    }

                    try {
                        // Update cache - wrapped in try-catch for database errors
                        val entities = products.map { product ->
                            FavoriteProductEntity.fromDomain(
                                product = product,
                                cachedAt = currentTime
                            )
                        }
                        favoriteProductDao.insertAll(products = entities)

                        // Clean up products that were unfavorited
                        favoriteProductDao.deleteNotInIds(ids = ids)
                    } catch (dbException: Exception) {
                        // Database update failed, but we still have the products from API
                        // Log the error but don't fail the entire operation
                        println("Warning: Failed to update favorites cache: ${dbException.message}")
                        // Flow won't emit, but at least we return the data
                    }

                    // Return domain products
                    products
                }
                .onFailure { error ->
                    // API call failed - log the error
                    println("Error: Failed to fetch favorites from API: ${error.message}")
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add a product to cache immediately (for newly favorited items)
     */
    suspend fun addToCache(product: Product) {
        try {
            val currentTime = currentTimeMillis()
            val entity = FavoriteProductEntity.fromDomain(product, currentTime)
            favoriteProductDao.insertAll(listOf(entity))
        } catch (e: Exception) {
            // Silently fail - cache insertion is not critical
        }
    }

    /**
     * Remove product from cache when unfavorited
     */
    suspend fun removeFromCache(productId: Long) {
        try {
            favoriteProductDao.deleteById(productId)
        } catch (e: Exception) {
            // Silently fail - cache cleanup is not critical
        }
    }

    /**
     * Clear all cached favorites
     */
    suspend fun clearCache() {
        try {
            favoriteProductDao.clearAll()
        } catch (e: Exception) {
            // Silently fail
        }
    }

    /**
     * Check if cache entry is expired
     */
    private fun isCacheExpired(cachedAt: Long): Boolean {
        return currentTimeMillis() - cachedAt > CACHE_EXPIRATION_MS
    }
}
