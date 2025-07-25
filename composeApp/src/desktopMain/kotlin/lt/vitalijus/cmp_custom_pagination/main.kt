package lt.vitalijus.cmp_custom_pagination

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMPCustomPagination",
    ) {
        App()
    }
}
