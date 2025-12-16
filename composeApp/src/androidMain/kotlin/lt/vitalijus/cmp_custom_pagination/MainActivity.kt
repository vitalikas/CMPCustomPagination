package lt.vitalijus.cmp_custom_pagination

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3.NavigationRoot
import lt.vitalijus.cmp_custom_pagination.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
//                RootScreen()
                NavigationRoot()
            }
        }
    }
}
