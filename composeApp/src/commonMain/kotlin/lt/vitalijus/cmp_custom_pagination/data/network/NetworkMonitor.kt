package lt.vitalijus.cmp_custom_pagination.data.network

import kotlinx.coroutines.flow.Flow

/**
 * Monitors network connectivity status across platforms
 */
interface NetworkMonitor {
    /**
     * Flow that emits true when connected to internet, false when disconnected
     */
    val isConnected: Flow<Boolean>
}
