package com.kotlisoft.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface NoteEntity : Entity<NoteEntity> {
    companion object : Entity.Factory<NoteEntity>()
    val id: Int?
    var note: String
}

object NotesEntity: Table<NoteEntity>("notes") {
    val id = int("id").primaryKey()
    val note = varchar("note")
}