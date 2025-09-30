package lt.vitalijus.cmp_custom_pagination.domain.di

import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase
import lt.vitalijus.cmp_custom_pagination.domain.usecase.products.LoadProductsUseCase
import org.koin.dsl.module

/**
 * Domain layer DI module
 * Contains pure business logic: use cases, domain services, etc.
 */
val domainModule = module {
    // Use Cases (Business Logic)
    single { LoadProductsUseCase(get()) }
    single { AddToBasketUseCase() }
}
