package com.kotlisoft

import com.kotlisoft.db.DatabaseFactory
import com.kotlisoft.entities.Notes
import io.ktor.server.application.*
import com.kotlisoft.plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

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
            var id = 6
            delay(60_000)
            var statement : InsertStatement<Number>? = null
            DatabaseFactory.dbQuery {
                statement = Notes.insert { note ->
                    note[Notes.id] = id
                    note[Notes.note] = "Max"
                }
            }
            id++
        }
    }
}
