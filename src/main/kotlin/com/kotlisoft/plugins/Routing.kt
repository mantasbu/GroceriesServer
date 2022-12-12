package com.kotlisoft.plugins

import com.kotlisoft.db.DatabaseFactory.dbQuery
import com.kotlisoft.entities.Notes
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
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Groceries Application")
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
