package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Strategy for handling background product updates without disrupting user experience.
 *
 * Problem: When WorkManager refreshes cache while user is browsing,
 * we need to update the cache without disrupting their current view.
 */
object BackgroundUpdateStrategy {

    /**
     * Merge background updates with current products intelligently.
     *
     * Strategy:
     * 1. Keep user's current scroll position
     * 2. Update products they've already seen (in cache)
     * 3. Don't modify the visible list order
     *
     * @param currentProducts Current products in state (visible to user)
     * @param updatedProducts Fresh products from background refresh
     * @return Merged list with updates applied
     */
    fun mergeBackgroundUpdates(
        currentProducts: List<Product>,
        updatedProducts: List<Product>
    ): List<Product> {
        if (currentProducts.isEmpty()) {
            return updatedProducts
        }

        // Create a map of updated products by ID for fast lookup
        val updatesById = updatedProducts.associateBy { it.id }

        // Update existing products with fresh data if available
        return currentProducts.map { currentProduct ->
            updatesById[currentProduct.id] ?: currentProduct
        }
    }

    /**
     * Check if an update should be applied immediately or deferred.
     *
     * Defer updates if:
     * - User is actively scrolling
     * - User is viewing a product detail
     * - Significant UI changes would be jarring
     */
    fun shouldApplyImmediately(
        isUserScrolling: Boolean,
        isDetailViewOpen: Boolean
    ): Boolean {
        return !isUserScrolling && !isDetailViewOpen
    }
}
