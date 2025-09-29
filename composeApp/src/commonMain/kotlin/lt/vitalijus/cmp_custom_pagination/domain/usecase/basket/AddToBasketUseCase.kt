package lt.vitalijus.cmp_custom_pagination.domain.usecase.basket

import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product

/**
 * Use case for adding products to basket with business rules
 * Single responsibility: Basket addition business logic
 */
class AddToBasketUseCase {

    fun execute(
        currentItems: List<BasketItem>,
        product: Product,
        quantity: Int
    ): Result<List<BasketItem>> {
        return try {
            require(quantity > 0) { "Quantity must be positive" }
            require(quantity <= MAX_QUANTITY_PER_ITEM) { "Cannot add more than $MAX_QUANTITY_PER_ITEM items" }
            require(product.id > 0) { "Product ID must be valid (positive)" }

            val existingItem = currentItems.find { it.product.id == product.id }

            val updatedItems = if (existingItem != null) {
                val newQuantity = existingItem.quantity + quantity
                require(newQuantity <= MAX_QUANTITY_PER_ITEM) { "Total quantity cannot exceed $MAX_QUANTITY_PER_ITEM" }

                // Update the specific item while keeping others unchanged
                // Using partition for better performance and clarity
                val (itemsToUpdate, otherItems) = currentItems.partition { it.product.id == product.id }

                // Should only have one item with this ID
                require(itemsToUpdate.size == 1) { "Data consistency error: multiple items with same product ID ${product.id}" }

                val updatedItem = itemsToUpdate.first().copy(quantity = newQuantity)
                otherItems + updatedItem
            } else {
                val newItem = BasketItem(product = product, quantity = quantity)
                currentItems + newItem
            }
            Result.success(updatedItems)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(IllegalStateException("Unexpected error in AddToBasketUseCase", e))
        }
    }

    companion object {

        private const val MAX_QUANTITY_PER_ITEM = 99
    }
}
