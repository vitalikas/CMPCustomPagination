package lt.vitalijus.cmp_custom_pagination.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Desktop implementation of NetworkMonitor with reliable ping verification
 * Uses socket connection to Google DNS to verify actual internet connectivity
 */
class DesktopNetworkMonitor : NetworkMonitor {
    
    override val isConnected: Flow<Boolean> = flow {
        var lastState: Boolean? = null
        
        while (true) {
            val currentState = pingGoogle()
            
            // Only emit when state changes
            if (currentState != lastState) {
                emit(currentState)
                lastState = currentState
            }
            
            // Check every 5 seconds
            delay(5000)
        }
    }
    
    /**
     * Ping Google DNS (8.8.8.8) to verify actual internet connectivity
     * More reliable than InetAddress.isReachable() which can be blocked by firewalls
     */
    private suspend fun pingGoogle(): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53) // Google DNS, port 53 (DNS)
            socket.connect(socketAddress, 3000) // 3 second timeout
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
