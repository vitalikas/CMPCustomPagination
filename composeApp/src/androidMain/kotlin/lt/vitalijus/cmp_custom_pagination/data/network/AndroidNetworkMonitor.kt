package lt.vitalijus.cmp_custom_pagination.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Android implementation of NetworkMonitor using ConnectivityManager with ping verification
 */
class AndroidNetworkMonitor(private val context: Context) : NetworkMonitor {
    
    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    override val isConnected: Flow<Boolean> = callbackFlow {
        // Send initial state with ping verification
        launch {
            val initialState = checkCurrentConnectivity() && pingGoogle()
            trySend(initialState)
        }
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Verify with ping before reporting as connected
                launch {
                    delay(500) // Small delay to let connection stabilize
                    val isActuallyConnected = pingGoogle()
                    trySend(isActuallyConnected)
                }
            }
            
            override fun onLost(network: Network) {
                trySend(false)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasNetworkConnection = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) && networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                
                if (hasNetworkConnection) {
                    // Verify actual internet access with ping
                    launch {
                        val hasInternet = pingGoogle()
                        trySend(hasInternet)
                    }
                } else {
                    trySend(false)
                }
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
    
    private fun checkCurrentConnectivity(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Ping Google DNS (8.8.8.8) to verify actual internet connectivity
     * This is a reliable way to check if we can actually reach the internet
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
