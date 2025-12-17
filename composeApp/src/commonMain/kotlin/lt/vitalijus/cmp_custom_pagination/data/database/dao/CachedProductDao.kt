package lt.vitalijus.cmp_custom_pagination.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import lt.vitalijus.cmp_custom_pagination.data.database.entity.CachedProductEntity

/**
 * DAO for cached products - implements offline-first strategy
 */
@Dao
interface CachedProductDao {

    /**
     * Get all cached products as a reactive Flow
     * Emits new data whenever the cache is updated
     */
    @Query("SELECT * FROM cached_products ORDER BY page ASC, id ASC")
    fun observeAll(): Flow<List<CachedProductEntity>>

    /**
     * Get all cached products (one-time query)
     */
    @Query("SELECT * FROM cached_products ORDER BY page ASC, id ASC")
    suspend fun getAll(): List<CachedProductEntity>

    /**
     * Get products for a specific page
     */
    @Query("SELECT * FROM cached_products WHERE page = :page ORDER BY id ASC")
    suspend fun getByPage(page: Int): List<CachedProductEntity>

    /**
     * Insert or update products in cache
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<CachedProductEntity>)

    /**
     * Get the oldest cached timestamp (to check if cache expired)
     */
    @Query("SELECT MIN(cachedAt) FROM cached_products")
    suspend fun getOldestCacheTimestamp(): Long?

    /**
     * Delete all cached products
     */
    @Query("DELETE FROM cached_products")
    suspend fun deleteAll()

    /**
     * Delete products older than a specific timestamp
     */
    @Query("DELETE FROM cached_products WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * Get count of cached products
     */
    @Query("SELECT COUNT(*) FROM cached_products")
    suspend fun getCount(): Int
}
