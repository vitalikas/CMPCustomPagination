package lt.vitalijus.cmp_custom_pagination.data.worker

import android.app.ActivityManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lt.vitalijus.cmp_custom_pagination.data.repository.ProductsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Background worker that automatically refreshes product cache every 2 minutes.
 *
 * Features:
 * - Runs only when device has internet (network constraint)
 * - Skips refresh if app is in foreground (prevents UI conflicts)
 * - Fetches fresh data from API
 * - Updates Room cache with new timestamps
 * - Works silently in background
 */
class ProductCacheRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val productsRepository: ProductsRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Skip refresh if app is in foreground (user might be browsing)
            if (isAppInForeground()) {
                println("⏭️ ProductCacheRefreshWorker: Skipped - App is active")
                return@withContext Result.success()
            }

            // Check if cache needs refresh
            val shouldRefresh = productsRepository.shouldRefresh()

            if (!shouldRefresh) {
                // Cache is still fresh, skip refresh
                println("⏭️ ProductCacheRefreshWorker: Cache is fresh, skipping")
                return@withContext Result.success()
            }

            // Refresh first page of products (most important)
            val result = productsRepository.refreshProducts(
                page = 0,
                pageSize = 30
            )

            result.fold(
                onSuccess = { products ->
                    // Successfully refreshed cache
                    println("✅ ProductCacheRefreshWorker: Refreshed ${products.size} products")
                    Result.success()
                },
                onFailure = { error ->
                    // Failed to refresh, retry later
                    println("❌ ProductCacheRefreshWorker: Failed - ${error.message}")
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            println("❌ ProductCacheRefreshWorker: Exception - ${e.message}")
            Result.retry()
        }
    }

    /**
     * Check if app is currently in foreground.
     * Returns true if user is actively using the app.
     */
    private fun isAppInForeground(): Boolean {
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses ?: return false

        return runningProcesses.any { processInfo ->
            processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    processInfo.processName == applicationContext.packageName
        }
    }

    companion object {
        const val WORK_NAME = "ProductCacheRefreshWork"
    }
}
