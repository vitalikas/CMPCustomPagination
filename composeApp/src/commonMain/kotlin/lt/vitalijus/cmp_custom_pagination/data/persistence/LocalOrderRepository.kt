package lt.vitalijus.cmp_custom_pagination.data.persistence

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod

class LocalOrderRepository(
    private val storage: KeyValueStorage
) : OrderRepository {

    private val mutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override suspend fun saveOrder(order: Order) = mutex.withLock {
        val orders = getOrders().toMutableList()
        orders.add(order)
        storage.putString(KEY_ORDERS, json.encodeToString(orders))
    }

    override suspend fun getOrders(): List<Order> = mutex.withLock {
        val ordersJson = storage.getString(KEY_ORDERS) ?: return emptyList()
        return try {
            json.decodeFromString<List<Order>>(ordersJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return getOrders().find { it.id == orderId }
    }

    override suspend fun updateOrder(order: Order) = mutex.withLock {
        val orders = getOrders().toMutableList()
        val index = orders.indexOfFirst { it.id == order.id }
        if (index != -1) {
            orders[index] = order
            storage.putString(KEY_ORDERS, json.encodeToString(orders))
        }
    }

    override suspend fun deleteOrder(orderId: String) = mutex.withLock {
        val orders = getOrders().toMutableList()
        orders.removeAll { it.id == orderId }
        storage.putString(KEY_ORDERS, json.encodeToString(orders))
    }

    override suspend fun clearAllOrders() = mutex.withLock {
        storage.remove(KEY_ORDERS)
    }

    override suspend fun saveDeliveryAddress(address: DeliveryAddress) = mutex.withLock {
        storage.putString(KEY_DELIVERY_ADDRESS, json.encodeToString(address))
    }

    override suspend fun getLastDeliveryAddress(): DeliveryAddress? = mutex.withLock {
        val addressJson = storage.getString(KEY_DELIVERY_ADDRESS) ?: return null
        return try {
            json.decodeFromString<DeliveryAddress>(addressJson)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun savePaymentMethod(method: PaymentMethod) = mutex.withLock {
        storage.putString(KEY_PAYMENT_METHOD, json.encodeToString(method))
    }

    override suspend fun getLastPaymentMethod(): PaymentMethod? = mutex.withLock {
        val methodJson = storage.getString(KEY_PAYMENT_METHOD) ?: return null
        return try {
            json.decodeFromString<PaymentMethod>(methodJson)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveFavorites(favoriteIds: Set<Long>) = mutex.withLock {
        storage.putString(KEY_FAVORITES, json.encodeToString(favoriteIds.toList()))
    }

    override suspend fun getFavorites(): Set<Long> = mutex.withLock {
        val favoritesJson = storage.getString(KEY_FAVORITES) ?: return emptySet()
        return try {
            json.decodeFromString<List<Long>>(favoritesJson).toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    companion object {
        private const val KEY_ORDERS = "orders"
        private const val KEY_DELIVERY_ADDRESS = "delivery_address"
        private const val KEY_PAYMENT_METHOD = "payment_method"
        private const val KEY_FAVORITES = "favorites"
    }
}

// Platform-specific storage interface
interface KeyValueStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}
