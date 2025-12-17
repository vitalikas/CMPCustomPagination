package lt.vitalijus.cmp_custom_pagination.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import lt.vitalijus.cmp_custom_pagination.data.database.entity.FavoriteProductEntity

/**
 * DAO for favorite products cache operations
 */
@Dao
interface FavoriteProductDao {

    /**
     * Observe all cached favorite products by their IDs (reactive)
     * This Flow automatically emits when the database changes
     */
    @Query("SELECT * FROM favorite_products WHERE id IN (:ids)")
    fun observeProductsByIds(ids: Set<Long>): Flow<List<FavoriteProductEntity>>

    /**
     * Get all cached favorite products by their IDs (one-time)
     */
    @Query("SELECT * FROM favorite_products WHERE id IN (:ids)")
    suspend fun getProductsByIds(ids: Set<Long>): List<FavoriteProductEntity>

    /**
     * Insert or replace favorite products in cache
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<FavoriteProductEntity>)

    /**
     * Delete a specific product from cache
     */
    @Query("DELETE FROM favorite_products WHERE id = :productId")
    suspend fun deleteById(productId: Long)

    /**
     * Delete products that are not in the provided ID list
     * (Clean up products that were unfavorited)
     */
    @Query("DELETE FROM favorite_products WHERE id NOT IN (:ids)")
    suspend fun deleteNotInIds(ids: Set<Long>)

    /**
     * Clear all cached favorite products
     */
    @Query("DELETE FROM favorite_products")
    suspend fun clearAll()

    /**
     * Get count of cached products
     */
    @Query("SELECT COUNT(*) FROM favorite_products")
    suspend fun getCount(): Int
}
