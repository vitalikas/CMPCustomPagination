package lt.vitalijus.cmp_custom_pagination.data.di

import lt.vitalijus.cmp_custom_pagination.data.repository.impl.ProductRepositoryImpl
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApi
import lt.vitalijus.cmp_custom_pagination.data.source.remote.api.ProductApiImpl
import lt.vitalijus.cmp_custom_pagination.domain.repository.ProductReader
import lt.vitalijus.cmp_custom_pagination.domain.repository.ProductRepository
import org.koin.dsl.module

val dataModule = module {
    single<ProductApi> { ProductApiImpl(get()) }

    single<ProductRepository> { ProductRepositoryImpl(get()) }

    single<ProductReader> { get<ProductRepository>() }
}
