package com.kotlisoft

import com.kotlisoft.db.DatabaseFactory
import com.kotlisoft.entities.Products
import io.ktor.server.application.*
import com.kotlisoft.plugins.*
import com.kotlisoft.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.insert
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()
    DatabaseFactory.init()
    launch {
        while (true) {

            val initialDoc = Jsoup.connect("https://www.tesco.com/groceries/en-GB/shop/fresh-food/all?page=1&count=48").get()

            val totalPages = initialDoc.select(".pagination--button")
                .select("span")
                .attr("aria-hidden", "true")
                .eachText()
                .last()
                .toInt()

            for (currentPage in 1..totalPages) {
                val doc = Jsoup.connect(
                    "https://www.tesco.com/groceries/en-GB/shop/fresh-food/all?page=$currentPage&count=48"
                ).get()

                val productIds = doc.select(".csVOnh")
                    .select("a")
                    .eachAttr("href")
                    .map { it.substringAfter("/groceries/en-GB/products/") }

                productIds.forEach { productId ->
                    println("Fetching productId: $productId")
                    val productDoc = Jsoup.connect(Constants.BASE_PRODUCT_URL.plus(productId)).get()

                    val title = productDoc.select("h1").text().replace("Tesco ", "")
                    val cost = productDoc.select(".value")
                        .select("span")
                        .firstOrNull {
                            it.hasAttr("data-auto")
                        }
                        ?.text()
                    var discountPrice = productDoc.select(".promo-content-small .offer-text")
                        .first()
                        ?.text()
                        ?.substringBefore(" Clubcard")
                        ?.substringAfter("Any ")

                    if (discountPrice != null && discountPrice.contains("p")) {
                        discountPrice = "0.".plus(discountPrice.substringBefore("p"))
                    }

                    val offerPeriod = productDoc.select(".dates").first()?.text()
                    val offerStart = offerPeriod?.substringAfter("from ")?.substringBefore(" until")
                    val offerEnd = offerPeriod?.substringAfter("until ")

                    DatabaseFactory.dbQuery {
                        Products.insert { product ->
                            product[productWebId] = productId
                            product[name] = title
                            product[price] = cost?.toFloat() ?: 0F
                            product[Products.discountPrice] = discountPrice
                            product[discountStartDate] = if (offerStart != null) LocalDate.parse(offerStart, DateTimeFormatter.ofPattern("dd/MM/yyyy")) else null
                            product[discountEndDate] = if (offerEnd != null) LocalDate.parse(offerEnd, DateTimeFormatter.ofPattern("dd/MM/yyyy")) else null
                            product[updatedAt] = LocalDate.now()
                        }
                    }
                }
            }

            delay(80_000_000)
        }
    }
}
