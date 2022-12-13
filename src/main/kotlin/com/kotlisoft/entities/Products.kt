package com.kotlisoft.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object Products : Table() {
    private val id: Column<Int> = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id, name = "id")
    val productWebId = varchar("product_web_id", 16)
    val name = varchar("name", 255)
    val price = float("price")
    val discountPrice = varchar("discount_price", 32).nullable()
    val discountStartDate = date("discount_start_date").nullable()
    val discountEndDate = date("discount_end_date").nullable()
    val updatedAt = date("updated_at")
}