package com.kotlisoft.plugins

import com.kotlisoft.db.DatabaseFactory.dbQuery
import com.kotlisoft.entities.Notes
import com.kotlisoft.models.Note
import com.kotlisoft.models.NoteRequest
import com.kotlisoft.models.NoteResponse
import com.kotlisoft.utils.Constants.BASE_PRODUCT_URL
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jsoup.Jsoup
import java.lang.StringBuilder

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Groceries Application")
        }
    }

    routing {
        get("/categories") {
            val doc = Jsoup.connect("https://www.tesco.com/groceries/").get()
            val result = doc.select(".menu__link--superdepartment")
                .select("a")
                .eachAttr("href")
            call.respondText(result.toString())
        }
    }

    routing {
        get("/products") {

            val doc = Jsoup.connect("https://www.tesco.com/groceries/en-GB/shop/fresh-food/all?page=1&count=48").get()
            val productLinks = doc.select(".csVOnh")
                .select("a")
                .eachAttr("href")
                .map { it.substringAfter("/groceries/en-GB/products/") }

            val result = StringBuilder()

            productLinks.forEach { productLink ->
                val productDoc = Jsoup.connect(BASE_PRODUCT_URL.plus(productLink)).get()

                val title = productDoc.select("h1").text().replace("Tesco ", "")
                val price = productDoc.select(".value")
                    .select("span")
                    .firstOrNull {
                        it.hasAttr("data-auto")
                    }
                    ?.text()
                val discountPrice = productDoc.select(".promo-content-small .offer-text")
                    .first()
                    ?.text()
                    ?.replace(" Clubcard Price", "")
                    ?.replace("p", "")
                val offerPeriod = productDoc.select(".dates").first()?.text()

                if (discountPrice != null) {
                    result.append("$title: price = $price, discounted price = $discountPrice, Validity: $offerPeriod\n")
                } else {
                    result.append("$title: price = $price\n")
                }
            }

            call.respond(result.toString())
        }
    }

    routing {
        get("/notes") {
            val result = dbQuery {
                Notes.selectAll().map { rowToNote(it) }
            }
            call.respond(result)
        }

        post("/notes") {
            val request = call.receive<NoteRequest>()

            var statement : InsertStatement<Number>? = null
            dbQuery {
                statement = Notes.insert { note ->
                    note[id] = 5
                    note[Notes.note] = request.note
                }
            }

            call.respond(
                HttpStatusCode.OK,
                NoteResponse(
                    success = true,
                    data = "Values has been successfully inserted"
                )
            )
        }
    }
}

private fun rowToNote(row: ResultRow?): Note? {
    if (row == null) {
        return null
    }
    return Note(
        id = row[Notes.id],
        note = row[Notes.note]
    )
}
