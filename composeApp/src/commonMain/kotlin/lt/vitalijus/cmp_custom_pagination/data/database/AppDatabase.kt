package lt.vitalijus.cmp_custom_pagination.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import lt.vitalijus.cmp_custom_pagination.data.database.dao.CachedProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.entity.CachedProductEntity
import lt.vitalijus.cmp_custom_pagination.data.database.entity.FavoriteProductEntity

/**
 * Room database for local caching
 */
@Database(
    entities = [
        FavoriteProductEntity::class,
        CachedProductEntity::class
    ],
    version = 2, // Incremented version for schema migration
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
    abstract fun cachedProductDao(): CachedProductDao
}
