package lt.vitalijus.cmp_custom_pagination.data.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * iOS implementation of NetworkMonitor using periodic connectivity checks
 * 
 * Simple implementation that assumes connectivity based on device status.
 * For production, consider using NWPathMonitor from Network framework.
 */
class IosNetworkMonitor : NetworkMonitor {
    
    override val isConnected: Flow<Boolean> = flow {
        // For iOS, we'll emit true by default
        // Real implementation would use NWPathMonitor, but it requires more complex setup
        emit(true)
        
        var lastState = true
        
        while (true) {
            // In a real implementation, check network reachability here
            // For now, just maintain connection status
            val currentState = true
            
            // Only emit when state changes
            if (currentState != lastState) {
                emit(currentState)
                lastState = currentState
            }
            
            // Check every 5 seconds
            delay(5000)
        }
    }
}
