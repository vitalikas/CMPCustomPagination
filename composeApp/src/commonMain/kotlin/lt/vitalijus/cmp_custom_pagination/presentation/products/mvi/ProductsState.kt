package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Single immutable state for the Products feature.
 */
data class ProductsState(
    val products: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList(),
    val favoriteProductIds: Set<Long> = emptySet(),
    val favoriteProductsData: List<Product> = emptyList(), // Products loaded specifically for favorites screen
    val productCache: Map<Long, Product> = emptyMap(), // Cache of all viewed products for instant access
    val isLoadingMore: Boolean = false,
    val isLoadingFavorites: Boolean = false,
    val error: String? = null,
    val currentDeliveryAddress: DeliveryAddress? = null,
    val currentPaymentMethod: PaymentMethod? = null,
    val currentOrder: Order? = null,
    val orders: List<Order> = emptyList()
) {
    // Computed properties (derived state)
    val totalQuantity: Int
        get() = basketItems.sumOf { it.quantity }

    val totalCostPrice: Double
        get() = basketItems.sumOf { it.product.price * 0.7 * it.quantity }

    val totalRetailPrice: Double
        get() = basketItems.sumOf { it.product.price * it.quantity }

    val isBasketEmpty: Boolean
        get() = basketItems.isEmpty()

    val favoriteProducts: List<Product>
        get() = products.filter { it.id in favoriteProductIds }
    
    /**
     * Find a product by ID from any available source (cache, products list, or favorites data).
     * This ensures instant access regardless of which tab the product was viewed from.
     */
    fun findProduct(productId: Long): Product? {
        // 1. Try cache first (fastest - O(1) lookup)
        productCache[productId]?.let { return it }
        
        // 2. Try current products list
        products.find { it.id == productId }?.let { return it }
        
        // 3. Try favorites data
        favoriteProductsData.find { it.id == productId }?.let { return it }
        
        // 4. Try basket items (product might only exist there)
        basketItems.find { it.product.id == productId }?.let { return it.product }
        
        return null
    }
}
