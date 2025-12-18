package lt.vitalijus.cmp_custom_pagination

import androidx.compose.ui.window.ComposeUIViewController
import lt.vitalijus.cmp_custom_pagination.data.di.iosDataModule
import lt.vitalijus.cmp_custom_pagination.di.appModule
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3.NavigationRoot
import org.koin.core.context.startKoin

/**
 * Flag to track if Koin has been initialized
 */
private var koinInitialized = false

/**
 * Initialize Koin once for the iOS app
 */
private fun initKoin() {
    if (!koinInitialized) {
        startKoin {
            modules(appModule + iosDataModule)
        }
        koinInitialized = true
    }
}

fun MainViewController() = ComposeUIViewController {
    // Initialize Koin once
    initKoin()
    
    NavigationRoot()
}
