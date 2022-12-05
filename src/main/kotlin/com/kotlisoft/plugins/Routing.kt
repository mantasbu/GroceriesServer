package com.kotlisoft.plugins

import com.kotlisoft.db.DatabaseConnection
import com.kotlisoft.db.DatabaseFactory
import com.kotlisoft.db.DatabaseFactory.dbQuery
import com.kotlisoft.entities.Notes
import com.kotlisoft.entities.NotesEntity
import com.kotlisoft.models.Note
import com.kotlisoft.models.NoteRequest
import com.kotlisoft.models.NoteResponse
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    routing {
        get("/notes") {
            //val db = DatabaseConnection.database
//            val notes = db.from(NotesEntity).select()
//                .map {
//                    val id = it[NotesEntity.id]
//                    val note = it[NotesEntity.note]
//                    Note(id ?: -1, note ?: "")
//                }
//            call.respond(notes)
            DatabaseFactory.init()
            val result = dbQuery {
                Notes.selectAll().map { rowToNote(it) }
            }
            call.respond(result)
        }

        post("/notes") {

            DatabaseFactory.init()

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

//            val db = DatabaseConnection.database
//            val request = call.receive<NoteRequest>()
//            val result = db.insert(NotesEntity) {
//                set(it.note, request.note)
//                set(it.id, 4)
//            }
//
//            if (result == 1) {
//                // Send successfully response to the client
//                call.respond(
//                    HttpStatusCode.OK,
//                    NoteResponse(
//                        success = true,
//                        data = "Values has been successfully inserted"
//                    )
//                )
//            } else {
//                // Send failure response to the client
//                call.respond(
//                    HttpStatusCode.BadRequest,
//                    NoteResponse(
//                        success = false,
//                        data = "Failed to insert values."
//                    )
//                )
//            }
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
