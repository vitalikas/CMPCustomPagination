package lt.vitalijus.cmp_custom_pagination.presentation.products

import lt.vitalijus.cmp_custom_pagination.domain.ProductPagerFactory
import org.koin.dsl.module

val viewModelModule = module {

    single<ProductPagerFactory> { ProductPagerFactoryImpl(get()) }
    single { ProductsViewModel(get()) }
}
