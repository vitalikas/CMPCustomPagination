package lt.vitalijus.cmp_custom_pagination.presentation.products.di

import lt.vitalijus.cmp_custom_pagination.domain.paging.CursorBasedPagingStrategy
import lt.vitalijus.cmp_custom_pagination.domain.paging.OffsetBasedPagingStrategy
import lt.vitalijus.cmp_custom_pagination.domain.paging.PagingStrategy
import lt.vitalijus.cmp_custom_pagination.domain.paging.ProductPagingFactory
import lt.vitalijus.cmp_custom_pagination.presentation.products.ProductsViewModel
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsStateMachine
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsTransitionState
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.DefaultScreenTitleProvider
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationController
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.NavigationManagerFactory
import lt.vitalijus.cmp_custom_pagination.presentation.products.navigation.ScreenTitleProvider
import org.koin.dsl.module

val presentationModule = module {
    // Navigation & UI Utilities
    single<ScreenTitleProvider> { DefaultScreenTitleProvider() }
    single<NavigationManagerFactory> {
        NavigationManagerFactory { navController ->
            NavigationController(navController)
        }
    }

    // Offset-based pagination (uses ProductRepository interface)
    single<PagingStrategy>(createdAtStart = true) { OffsetBasedPagingStrategy(repository = get()) }

    // Cursor-based pagination (uses CursorProductReader interface)  
//    single<PagingStrategy> { CursorBasedPagingStrategy(cursorReader = get()) }

    // Factory for creating ProductPager instances
    single(createdAtStart = true) { ProductPagingFactory(get()) }

    // MVI Components
    factory { ProductsStateMachine(initialState = ProductsTransitionState.Idle) }

    // ViewModels
    single {
        ProductsViewModel(
            pagerFactory = get(),
            addToBasketUseCase = get(),
            stateMachine = get(),
            orderRepository = get(),
            favoritesRepository = get(),
            productsRepository = get()
        )
    }
}
