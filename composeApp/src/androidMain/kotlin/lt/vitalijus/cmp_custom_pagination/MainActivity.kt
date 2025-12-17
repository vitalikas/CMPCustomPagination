package lt.vitalijus.cmp_custom_pagination

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import lt.vitalijus.cmp_custom_pagination.data.worker.WorkManagerScheduler
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3.NavigationRoot
import lt.vitalijus.cmp_custom_pagination.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Schedule automatic background cache refresh every 2 minutes
        WorkManagerScheduler.scheduleProductCacheRefresh(this)

        setContent {
            AppTheme {
//                RootScreen()
                NavigationRoot()
            }
        }
    }
}
