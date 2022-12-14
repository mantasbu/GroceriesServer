package com.kotlisoft

import com.kotlisoft.db.DatabaseFactory
import com.kotlisoft.entities.Notes
import io.ktor.server.application.*
import com.kotlisoft.plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.insert
import org.jsoup.Jsoup

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
            val doc = Jsoup.connect("https://www.tesco.com/").get()
            val title = doc.title()
            DatabaseFactory.dbQuery {
                Notes.insert { note ->
                    note[id] = 10
                    note[Notes.note] = title
                }
            }
            delay(80_000_000)
        }
    }
}
