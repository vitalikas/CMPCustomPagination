package lt.vitalijus.cmp_custom_pagination

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import lt.vitalijus.cmp_custom_pagination.di.appModule
import lt.vitalijus.cmp_custom_pagination.presentation.navigation.AppNavigation
import org.koin.core.context.startKoin

fun main() {
    val koin = startKoin {
        modules(appModule)
    }.koin

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CMPCustomPagination",
        ) {
            AppNavigation()
        }
    }
}
