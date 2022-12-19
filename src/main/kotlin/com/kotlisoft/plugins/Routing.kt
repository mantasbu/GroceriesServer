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

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Groceries Application")
        }
    }

    routing {
        get("/search") {
            val name = call.request.queryParameters["name"]
            if (!name.isNullOrBlank() && name.length > 2) {
                val result = dbQuery {
                    Products.selectAll()
                        .map { rowToProduct(it) }
                        .filter { it!!.name.lowercase().contains(name.lowercase()) }
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
        get("/notes") {
            val result = dbQuery {
                Notes.selectAll().map { rowToNote(it) }
            }
            call.respond(result)
        }
    }
}
