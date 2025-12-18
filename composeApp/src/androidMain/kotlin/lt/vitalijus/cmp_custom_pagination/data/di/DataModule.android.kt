package lt.vitalijus.cmp_custom_pagination.data.di

import lt.vitalijus.cmp_custom_pagination.data.database.AppDatabase
import lt.vitalijus.cmp_custom_pagination.data.database.dao.CachedProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.getDatabaseBuilder
import lt.vitalijus.cmp_custom_pagination.data.network.AndroidNetworkMonitor
import lt.vitalijus.cmp_custom_pagination.data.network.NetworkMonitor
import lt.vitalijus.cmp_custom_pagination.data.persistence.AndroidKeyValueStorage
import lt.vitalijus.cmp_custom_pagination.data.persistence.KeyValueStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDataModule = module {
    single<KeyValueStorage> { AndroidKeyValueStorage(get()) }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }

    // Room Database
    single<AppDatabase> {
        getDatabaseBuilder(androidContext())
            .fallbackToDestructiveMigration(true) // For development - handle migration properly in production!
            .build()
    }
    single<FavoriteProductDao> { get<AppDatabase>().favoriteProductDao() }
    single<CachedProductDao> { get<AppDatabase>().cachedProductDao() }
}
