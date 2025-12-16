package lt.vitalijus.cmp_custom_pagination

import android.app.Application
import io.kotzilla.sdk.analytics.koin.analytics
import lt.vitalijus.cmp_custom_pagination.data.di.androidDataModule
import lt.vitalijus.cmp_custom_pagination.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule + androidDataModule)

            // Kotzilla analytics
            analytics {
                setApiKey("ktz-sdk-GeCjRSUNb1FL1iQyMJHTTMc-hYVe_0bHhWU2aLSqMn8") // from kotzilla.json
            }
        }
    }
}
