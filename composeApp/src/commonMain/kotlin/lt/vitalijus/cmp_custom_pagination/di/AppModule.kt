package lt.vitalijus.cmp_custom_pagination.di

import lt.vitalijus.cmp_custom_pagination.core.network.networkModule
import lt.vitalijus.cmp_custom_pagination.data.di.dataModule
import lt.vitalijus.cmp_custom_pagination.presentation.products.viewModelModule

val appModule = listOf(
    networkModule,
    dataModule,
    viewModelModule
)
