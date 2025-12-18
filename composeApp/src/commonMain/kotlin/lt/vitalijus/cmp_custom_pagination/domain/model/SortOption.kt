package lt.vitalijus.cmp_custom_pagination.domain.model

/**
 * Available sorting options for product lists
 */
enum class SortOption(val displayName: String) {
    NONE("Default"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    RATING("Rating"),
    NAME("Name (A-Z)")
}
