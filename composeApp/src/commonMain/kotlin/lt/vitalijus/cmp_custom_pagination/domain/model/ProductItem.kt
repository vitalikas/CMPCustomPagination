package lt.vitalijus.cmp_custom_pagination.domain.model

data class Product(
    val id: Long,
    val title: String,
    val price: Double,
    val description: String? = null,
    val category: String? = null,
    val brand: String? = null,
    val thumbnail: String? = null
) {
    val costPrice: Double
        get() = price * 0.7

    val retailPrice: Double
        get() = price
}

data class ProductItem(
    val products: List<Product>,
    val total: Long,
    val nextCursor: String? = null
)
