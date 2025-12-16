package lt.vitalijus.cmp_custom_pagination.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.entity.FavoriteProductEntity

/**
 * Room database for local caching
 */
@Database(
    entities = [FavoriteProductEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
}
