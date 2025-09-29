package lt.vitalijus.cmp_custom_pagination.presentation.products

import lt.vitalijus.cmp_custom_pagination.domain.ProductPagerFactory
import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase
import lt.vitalijus.cmp_custom_pagination.domain.usecase.products.LoadProductsUseCase
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.DefaultScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.NavigationController
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.NavigationManagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.ScreenTitleProvider
import org.koin.dsl.module

val viewModelModule = module {
    // Use Cases (Business Logic)
    single { LoadProductsUseCase(get()) }
    single { AddToBasketUseCase() }

    // Navigation & UI Utilities
    single<ScreenTitleProvider> { DefaultScreenTitleProvider() }
    single<NavigationManagerFactory> {
        NavigationManagerFactory { navController ->
            NavigationController(navController)
        }
    }

    // Factories & ViewModels
    single<ProductPagerFactory> { ProductPagerFactoryImpl(get()) }
    single { ProductsViewModel(get(), get()) }
}
