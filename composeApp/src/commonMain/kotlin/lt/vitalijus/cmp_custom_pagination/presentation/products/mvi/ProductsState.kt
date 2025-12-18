package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.SortOption
import lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference

/**
 * Single immutable state for the Products feature.
 */
data class ProductsState(
    val products: List<Product> = emptyList(),
    val basketItems: List<BasketItem> = emptyList(),
    val favoriteProductIds: Set<Long> = emptySet(),
    val favoriteProductsData: List<Product> = emptyList(), // Products loaded specifically for favorites screen
    val productCache: Map<Long, Product> = emptyMap(), // Cache of all viewed products for instant access
    val searchQuery: String = "", // Current search query
    val sortOption: SortOption = SortOption.NONE, // Current sort option
    val viewLayoutMode: ViewLayoutPreference = ViewLayoutPreference.GRID, // Current view layout (Grid/List)
    val isLoadingMore: Boolean = false,
    val isLoadingFavorites: Boolean = false,
    val isLoadingAllItems: Boolean = false, // Loading all items for sorting
    val isConnectedToInternet: Boolean = true, // Network connectivity status
    val allItemsLoaded: Boolean = false, // All items have been loaded
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
     * Filtered products based on search query and sort option.
     * Searches in title, brand, category, and description.
     * Applies sorting dynamically based on the current sortOption.
     */
    val filteredProducts: List<Product>
        get() {
            val filtered = if (searchQuery.isBlank()) {
                products
            } else {
                products.filter { product ->
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.brand?.contains(searchQuery, ignoreCase = true) == true ||
                    product.category?.contains(searchQuery, ignoreCase = true) == true ||
                    product.description?.contains(searchQuery, ignoreCase = true) == true
                }
            }
            
            // Apply sorting to filtered results
            return when (sortOption) {
                SortOption.NONE -> filtered
                SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.price }
                SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.price }
                SortOption.RATING -> filtered.sortedByDescending { it.rating ?: 0.0 }
                SortOption.NAME -> filtered.sortedBy { it.title }
            }
        }
    
    /**
     * Filtered favorite products based on search query and sort option.
     */
    val filteredFavoriteProducts: List<Product>
        get() {
            val filtered = if (searchQuery.isBlank()) {
                favoriteProductsData
            } else {
                favoriteProductsData.filter { product ->
                    product.title.contains(searchQuery, ignoreCase = true) ||
                    product.brand?.contains(searchQuery, ignoreCase = true) == true ||
                    product.category?.contains(searchQuery, ignoreCase = true) == true ||
                    product.description?.contains(searchQuery, ignoreCase = true) == true
                }
            }
            
            return when (sortOption) {
                SortOption.NONE -> filtered
                SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.price }
                SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.price }
                SortOption.RATING -> filtered.sortedByDescending { it.rating ?: 0.0 }
                SortOption.NAME -> filtered.sortedBy { it.title }
            }
        }
    
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
