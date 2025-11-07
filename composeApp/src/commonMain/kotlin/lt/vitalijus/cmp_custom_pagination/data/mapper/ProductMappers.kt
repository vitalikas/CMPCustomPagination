package lt.vitalijus.cmp_custom_pagination.data.mapper

import lt.vitalijus.cmp_custom_pagination.data.model.DimensionsDto
import lt.vitalijus.cmp_custom_pagination.data.model.ProductDto
import lt.vitalijus.cmp_custom_pagination.data.model.ProductResponseDto
import lt.vitalijus.cmp_custom_pagination.data.model.ReviewDto
import lt.vitalijus.cmp_custom_pagination.domain.model.Dimensions
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Review

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        brand = brand,
        thumbnail = thumbnail,
        rating = rating,
        stock = stock,
        tags = tags,
        sku = sku,
        weight = weight,
        dimensions = dimensions?.toDimensions(),
        warrantyInformation = warrantyInformation,
        shippingInformation = shippingInformation,
        availabilityStatus = availabilityStatus,
        reviews = reviews?.map { it.toReview() },
        returnPolicy = returnPolicy,
        minimumOrderQuantity = minimumOrderQuantity,
        images = images,
        discountPercentage = discountPercentage
    )
}

fun DimensionsDto.toDimensions(): Dimensions {
    return Dimensions(
        width = width,
        height = height,
        depth = depth
    )
}

fun ReviewDto.toReview(): Review {
    return Review(
        rating = rating,
        comment = comment,
        date = date,
        reviewerName = reviewerName,
        reviewerEmail = reviewerEmail
    )
}

fun ProductResponseDto.toProductItem(): ProductItem {
    return ProductItem(
        products = products.map { it.toProduct() },
        total = total
    )
}
