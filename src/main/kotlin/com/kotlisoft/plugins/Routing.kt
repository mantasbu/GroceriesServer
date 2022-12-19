package com.kotlisoft.plugins

import com.kotlisoft.db.DatabaseFactory.dbQuery
import com.kotlisoft.entities.Notes
import com.kotlisoft.entities.Products
import com.kotlisoft.models.rowToNote
import com.kotlisoft.models.rowToProduct
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Groceries Application")
        }
    }

    routing {
        get("/search") {
            val name = call.request.queryParameters["name"]
            val hasDiscount = call.request.queryParameters["hasDiscount"]
            if (!name.isNullOrBlank() && name.length > 2) {
                val result = dbQuery {
                    Products.selectAll()
                        .map { rowToProduct(it) }
                        .filter { product ->
                            if (product != null && hasDiscount.isNullOrBlank()) {
                                product.name.lowercase().contains(name.lowercase())
                            } else if (product != null && !hasDiscount.isNullOrBlank()) {
                                product.name.lowercase().contains(name.lowercase()) &&
                                        product.discountEndDate != null &&
                                        product.discountEndDate.isBefore(LocalDate.now())
                            } else {
                                false
                            }
                        }
                }
                call.respond(result)
            } else if (!name.isNullOrBlank() && name.length < 3) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "Name is not valid"
                )
            } else {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "Name cannot be blank"
                )
            }
        }
    }

    routing {
        get("/discount") {
            val result = dbQuery {
                Products.selectAll()
                    .map { rowToProduct(it) }
                    .filter { product ->
                        product?.discountEndDate != null && product.discountEndDate.isBefore(LocalDate.now())
                    }
            }
            if (result.isNotEmpty()) {
                call.respond(result)
            } else {
                call.respond("There are no products on discount")
            }
        }
    }

    routing {
        get("/notes") {
            val result = dbQuery {
                Notes.selectAll().map { rowToNote(it) }
            }
            call.respond(result)
        }
    }
}
