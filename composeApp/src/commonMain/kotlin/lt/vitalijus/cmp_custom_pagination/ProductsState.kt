package lt.vitalijus.cmp_custom_pagination

data class ProductsState(
    val products: List<ProductDto> = emptyList(),
    val isLoadingMore: Boolean = false,
    val error: String? = null
)
