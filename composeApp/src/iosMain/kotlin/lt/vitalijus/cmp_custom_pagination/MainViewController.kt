package lt.vitalijus.cmp_custom_pagination

import androidx.compose.ui.window.ComposeUIViewController
import lt.vitalijus.cmp_custom_pagination.di.appModule
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.AppNavigation
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }

    AppNavigation()
}