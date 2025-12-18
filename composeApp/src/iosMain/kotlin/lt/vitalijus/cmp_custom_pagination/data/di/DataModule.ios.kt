package lt.vitalijus.cmp_custom_pagination.data.di

import lt.vitalijus.cmp_custom_pagination.data.database.AppDatabase
import lt.vitalijus.cmp_custom_pagination.data.database.dao.CachedProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.getDatabaseBuilder
import lt.vitalijus.cmp_custom_pagination.data.network.IosNetworkMonitor
import lt.vitalijus.cmp_custom_pagination.data.network.NetworkMonitor
import lt.vitalijus.cmp_custom_pagination.data.persistence.IosKeyValueStorage
import lt.vitalijus.cmp_custom_pagination.data.persistence.KeyValueStorage
import org.koin.dsl.module

val iosDataModule = module {
    single<KeyValueStorage> { IosKeyValueStorage() }
    single<NetworkMonitor> { IosNetworkMonitor() }
    
    // Room Database
    single<AppDatabase> {
        try {
            println("🔧 iOS: Building AppDatabase...")
            val db = getDatabaseBuilder()
                .fallbackToDestructiveMigration(true) // For development - handle migration properly in production!
                .build()
            println("✅ iOS: AppDatabase built successfully")
            db
        } catch (e: Exception) {
            println("❌ iOS: Failed to build AppDatabase: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    single<FavoriteProductDao> {
        println("🔧 iOS: Creating FavoriteProductDao...")
        get<AppDatabase>().favoriteProductDao()
    }
    single<CachedProductDao> {
        println("🔧 iOS: Creating CachedProductDao...")
        get<AppDatabase>().cachedProductDao()
    }
}
