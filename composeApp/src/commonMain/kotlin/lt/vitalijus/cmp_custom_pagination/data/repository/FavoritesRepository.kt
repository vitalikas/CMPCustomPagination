package lt.vitalijus.cmp_custom_pagination.data.repository

import lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.entity.FavoriteProductEntity
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Repository for managing favorite products with cache-first strategy.
 *
 * Strategy:
 * 1. Return cached data immediately if available and fresh
 * 2. Fetch from network in background if cache is expired
 * 3. Update cache with fresh data
 */
class FavoritesRepository(
    private val favoriteProductDao: FavoriteProductDao,
    private val productApi: ProductApi
) {
    companion object {
        private const val CACHE_EXPIRATION_MS = 3_600_000L // 1 hour
    }

    /**
     * Get cached favorites (instant, no network call)
     */
    suspend fun getCachedFavorites(ids: Set<Long>): Result<List<Product>> {
        if (ids.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            val cached = favoriteProductDao.getProductsByIds(ids)
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
            val cached = favoriteProductDao.getProductsByIds(ids)

            // Refresh if:
            // 1. No cached data
            // 2. Not all products are cached
            // 3. Any product is expired
            cached.isEmpty() ||
                    cached.size != ids.size ||
                    cached.any { isCacheExpired(it.cachedAt) }
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
            productApi.getProductsByIds(ids).map { productDtos ->
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

                // Update cache
                val entities = products.map { product ->
                    FavoriteProductEntity.fromDomain(product, currentTime)
                }
                favoriteProductDao.insertAll(entities)

                // Clean up products that were unfavorited
                favoriteProductDao.deleteNotInIds(ids)

                products
            }
        } catch (e: Exception) {
            Result.failure(e)
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
