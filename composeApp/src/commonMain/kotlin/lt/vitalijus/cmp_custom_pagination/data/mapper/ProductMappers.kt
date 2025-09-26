package lt.vitalijus.cmp_custom_pagination.data.mapper

import lt.vitalijus.cmp_custom_pagination.data.model.ProductDto
import lt.vitalijus.cmp_custom_pagination.data.model.ProductResponseDto
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        brand = brand,
        thumbnail = thumbnail
    )
}

fun ProductResponseDto.toProductItem(): ProductItem {
    return ProductItem(
        products = products.map { it.toProduct() },
        total = total
    )
}
