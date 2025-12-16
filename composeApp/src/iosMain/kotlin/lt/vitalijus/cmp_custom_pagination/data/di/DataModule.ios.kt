package lt.vitalijus.cmp_custom_pagination.data.di

import lt.vitalijus.cmp_custom_pagination.data.database.AppDatabase
import lt.vitalijus.cmp_custom_pagination.data.database.dao.FavoriteProductDao
import lt.vitalijus.cmp_custom_pagination.data.database.getDatabaseBuilder
import lt.vitalijus.cmp_custom_pagination.data.persistence.IosKeyValueStorage
import lt.vitalijus.cmp_custom_pagination.data.persistence.KeyValueStorage
import org.koin.dsl.module

val iosDataModule = module {
    single<KeyValueStorage> { IosKeyValueStorage() }
    
    // Room Database
    single<AppDatabase> { 
        getDatabaseBuilder()
            .build()
    }
    single<FavoriteProductDao> { get<AppDatabase>().favoriteProductDao() }
}
