package lt.vitalijus.cmp_custom_pagination.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Scheduler for automatic background cache refresh using WorkManager.
 * 
 * Configuration:
 * - Runs every 15 minutes (minimum allowed by WorkManager for periodic work)
 * - Requires internet connection (CONNECTED constraint)
 * - Survives app restart
 * - Battery-efficient
 */
object WorkManagerScheduler {

    private const val REFRESH_INTERVAL_MINUTES = 15L

    /**
     * Schedule periodic cache refresh.
     * Call this once on app startup.
     */
    fun scheduleProductCacheRefresh(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when internet available
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ProductCacheRefreshWorker>(
            repeatInterval = REFRESH_INTERVAL_MINUTES,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Use KEEP policy: if work already scheduled, don't replace it
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ProductCacheRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing schedule
            workRequest
        )

        println("📅 WorkManager: Scheduled product cache refresh every $REFRESH_INTERVAL_MINUTES minutes")
    }

    /**
     * Cancel the periodic refresh (e.g., on user logout)
     */
    fun cancelProductCacheRefresh(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ProductCacheRefreshWorker.WORK_NAME)
        println("🛑 WorkManager: Cancelled product cache refresh")
    }

    /**
     * Check if refresh is currently scheduled
     */
    fun isScheduled(context: Context): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(ProductCacheRefreshWorker.WORK_NAME)
            .get()
        return workInfos.isNotEmpty()
    }
}
