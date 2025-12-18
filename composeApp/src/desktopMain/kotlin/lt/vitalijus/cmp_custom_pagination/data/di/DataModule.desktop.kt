package lt.vitalijus.cmp_custom_pagination.data.di

import lt.vitalijus.cmp_custom_pagination.data.network.DesktopNetworkMonitor
import lt.vitalijus.cmp_custom_pagination.data.network.NetworkMonitor
import org.koin.dsl.module

val desktopDataModule = module {
    single<NetworkMonitor> { DesktopNetworkMonitor() }
}
