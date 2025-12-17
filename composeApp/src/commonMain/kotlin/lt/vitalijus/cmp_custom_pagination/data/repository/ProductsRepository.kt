package lt.vitalijus.cmp_custom_pagination.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis
import lt.vitalijus.cmp_custom_pagination.data.database.dao.CachedProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.entity.CachedProductEntity
import lt.vitalijus.cmp_custom_pagination.data.mapper.toProduct
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Repository for Products screen with offline-first strategy.
 * 
 * Strategy:
 * 1. Observe cached products via Flow (instant display)
 * 2. Check cache age - if > 1 minute, refresh from network
 * 3. Network data is automatically inserted to cache
 * 4. Flow emits updated data automatically
 */
class ProductsRepository(
    private val productApi: ProductApi,
    private val cachedProductDao: CachedProductDao
) {
    companion object {
        private const val CACHE_VALIDITY_MS = 60_000L // 1 minute
    }

    /**
     * Observe all cached products as a Flow.
     * Automatically emits when cache is updated.
     */
    fun observeCachedProducts(): Flow<List<Product>> {
        return cachedProductDao.observeAll()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    /**
     * Get cached products (one-time query)
     */
    suspend fun getCachedProducts(): Result<List<Product>> {
        return try {
            val cached = cachedProductDao.getAll().map { it.toDomain() }
            Result.success(cached)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if cache should be refreshed (older than 1 minute)
     */
    suspend fun shouldRefresh(): Boolean {
        val oldestTimestamp = cachedProductDao.getOldestCacheTimestamp()
        if (oldestTimestamp == null) {
            // No cache exists
            return true
        }

        val now = currentTimeMillis()
        val cacheAge = now - oldestTimestamp
        return cacheAge > CACHE_VALIDITY_MS
    }

    /**
     * Fetch products from network and update cache.
     * For pagination, specify page and pageSize.
     */
    suspend fun refreshProducts(
        page: Int = 0,
        pageSize: Int = 30
    ): Result<List<Product>> {
        return try {
            val result = productApi.getProducts(page = page, pageSize = pageSize)
            
            result.map { productResponseDto ->
                val now = currentTimeMillis()
                val products = productResponseDto.products.map { it.toProduct() }
                
                // Convert to entities with current timestamp
                val entities = products.map { product ->
                    CachedProductEntity.fromDomain(
                        product = product,
                        cachedAt = now,
                        page = page
                    )
                }
                
                // Insert into cache - Room will emit via Flow automatically!
                cachedProductDao.insertAll(entities)
                
                products
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save already-loaded products to cache (without fetching from API)
     * Useful when products are loaded via pager
     */
    suspend fun cacheProducts(products: List<Product>, page: Int = 0) {
        val now = currentTimeMillis()
        val entities = products.map { product ->
            CachedProductEntity.fromDomain(
                product = product,
                cachedAt = now,
                page = page
            )
        }
        cachedProductDao.insertAll(entities)
    }

    /**
     * Clear all cached products (e.g., on logout or manual refresh)
     */
    suspend fun clearCache() {
        cachedProductDao.deleteAll()
    }

    /**
     * Delete old cache entries (older than validity period)
     */
    suspend fun cleanupOldCache() {
        val cutoffTime = currentTimeMillis() - CACHE_VALIDITY_MS
        cachedProductDao.deleteOlderThan(cutoffTime)
    }

    /**
     * Get cache statistics (for debugging)
     */
    suspend fun getCacheInfo(): CacheInfo {
        val count = cachedProductDao.getCount()
        val oldestTimestamp = cachedProductDao.getOldestCacheTimestamp()
        val age = oldestTimestamp?.let { currentTimeMillis() - it }
        
        return CacheInfo(
            count = count,
            ageMs = age,
            isExpired = age?.let { it > CACHE_VALIDITY_MS } ?: true
        )
    }

    data class CacheInfo(
        val count: Int,
        val ageMs: Long?,
        val isExpired: Boolean
    )
}
