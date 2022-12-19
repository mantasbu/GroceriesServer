package com.kotlisoft.utils

import com.kotlisoft.db.DatabaseFactory
import com.kotlisoft.entities.Products
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.insert
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Scraper {

    suspend fun scrapeTescoFreshFoodData() {
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