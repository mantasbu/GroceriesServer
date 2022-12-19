package com.kotlisoft.models

import com.kotlisoft.entities.Notes
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Note(
    val id: Int,
    val note: String
)

fun rowToNote(row: ResultRow?): Note? {
    if (row == null) {
        return null
    }
    return Note(
        id = row[Notes.id],
        note = row[Notes.note]
    )
}
