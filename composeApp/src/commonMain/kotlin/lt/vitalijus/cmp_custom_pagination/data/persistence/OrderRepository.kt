package lt.vitalijus.cmp_custom_pagination.data.persistence

import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod

interface OrderRepository {
    suspend fun saveOrder(order: Order)
    suspend fun getOrders(): List<Order>
    suspend fun getOrderById(orderId: String): Order?
    suspend fun updateOrder(order: Order)
    suspend fun deleteOrder(orderId: String)
    suspend fun clearAllOrders()

    suspend fun saveDeliveryAddress(address: DeliveryAddress)
    suspend fun getLastDeliveryAddress(): DeliveryAddress?

    suspend fun savePaymentMethod(method: PaymentMethod)
    suspend fun getLastPaymentMethod(): PaymentMethod?

    suspend fun saveFavorites(favoriteIds: Set<Long>)
    suspend fun getFavorites(): Set<Long>
}
