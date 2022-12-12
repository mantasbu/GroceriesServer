package com.kotlisoft

import com.kotlisoft.db.DatabaseFactory
import io.ktor.server.application.*
import com.kotlisoft.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()
    DatabaseFactory.init()
}
