package com.kotlisoft.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object ProductsEntity : Table() {
    private val id: Column<Int> = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id, name = "id")
    val productWebId = varchar("product_web_id", 32)
    val name = varchar("name", 255)
    val category = varchar("category", 32)
    val price = decimal("price", 5, 2)
    val pricePerUnit = varchar("price_per_unit", 8)
    val discountPrice = decimal("discount_price", 5, 2)
    val discountStartDate = date("discount_start_date")
    val discountEndDate = date("discount_end_date")
    val updatedAt = date("updated_at")
}