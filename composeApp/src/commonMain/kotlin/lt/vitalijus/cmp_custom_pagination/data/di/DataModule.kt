package lt.vitalijus.cmp_custom_pagination.data.di

import lt.vitalijus.cmp_custom_pagination.data.persistence.LocalOrderRepository
import lt.vitalijus.cmp_custom_pagination.data.persistence.OrderRepository
import lt.vitalijus.cmp_custom_pagination.data.repository.FavoritesRepository
import lt.vitalijus.cmp_custom_pagination.data.repository.impl.InMemoryProductRepository
import lt.vitalijus.cmp_custom_pagination.data.repository.impl.OffsetBasedProductRepository
import lt.vitalijus.cmp_custom_pagination.data.source.local.InMemoryProductDataSource
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApiImpl
import lt.vitalijus.cmp_custom_pagination.domain.repository.CursorProductReader
import lt.vitalijus.cmp_custom_pagination.domain.repository.OffsetBasedProductReader
import org.koin.dsl.module

val dataModule = module {
    single<ProductApi> { ProductApiImpl(get()) }

    single { InMemoryProductDataSource() }
    single<CursorProductReader> { InMemoryProductRepository(get()) }

    single<OffsetBasedProductReader> { OffsetBasedProductRepository(get()) }

    // Persistence
    // Note: KeyValueStorage and AppDatabase are provided by platform-specific modules
    single<OrderRepository> { LocalOrderRepository(storage = get()) }
    single { FavoritesRepository(favoriteProductDao = get(), productApi = get()) }
}
