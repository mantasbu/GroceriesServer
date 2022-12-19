package com.kotlisoft.models

import com.kotlisoft.entities.Products
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDate

@Serializable
data class Product(
    val productWedId: String,
    val name: String,
    val price: Float,
    val discountPrice: String? = null,
    @Contextual val discountStartDate: LocalDate? = null,
    @Contextual val discountEndDate: LocalDate? = null,
    @Contextual val updatedAt: LocalDate,
)

fun rowToProduct(row: ResultRow?): Product? {
    if (row == null) {
        return null
    }
    return Product(
        productWedId = row[Products.productWebId],
        name = row[Products.name],
        price = row[Products.price],
        discountPrice = row[Products.discountPrice],
        discountStartDate = row[Products.discountStartDate],
        discountEndDate = row[Products.discountEndDate],
        updatedAt = row[Products.updatedAt],
    )
}