package lt.vitalijus.cmp_custom_pagination.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import lt.vitalijus.cmp_custom_pagination.data.database.converters.Converters
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
    version = 5, // Incremented for adding rating field
    exportSchema = false
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
    abstract fun cachedProductDao(): CachedProductDao
}

// Required for Kotlin Multiplatform Room
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
