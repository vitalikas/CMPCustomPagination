package lt.vitalijus.cmp_custom_pagination.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val items: List<BasketItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val deliveryAddress: DeliveryAddress,
    val paymentMethod: PaymentMethod,
    val createdAt: Long,
    val estimatedDeliveryTime: Long,
    val actualDeliveryTime: Long? = null,
    val ratings: Map<Long, ProductRating> = emptyMap() // productId to rating
)

@Serializable
enum class OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_PROCESSING,
    PAYMENT_CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

@Serializable
data class DeliveryAddress(
    val fullName: String,
    val addressLine1: String,
    val addressLine2: String = "",
    val city: String,
    val postalCode: String,
    val phoneNumber: String
)

@Serializable
data class PaymentMethod(
    val type: PaymentType,
    val cardLastFourDigits: String? = null
)

@Serializable
enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    CASH_ON_DELIVERY
}

@Serializable
data class ProductRating(
    val productId: Long,
    val rating: Int, // 1-5
    val comment: String,
    val createdAt: Long
)
