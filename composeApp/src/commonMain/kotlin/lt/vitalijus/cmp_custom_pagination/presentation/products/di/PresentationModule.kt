package lt.vitalijus.cmp_custom_pagination.presentation.products.di

import lt.vitalijus.cmp_custom_pagination.domain.ProductPagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductPagerFactoryImpl
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.DefaultScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationController
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationManagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.ScreenTitleProvider
import org.koin.dsl.module

/**
 * Presentation layer DI module
 * Contains UI-related dependencies: ViewModels, navigation, UI utilities
 */
val presentationModule = module {
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
