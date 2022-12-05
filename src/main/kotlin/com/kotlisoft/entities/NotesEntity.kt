package com.kotlisoft.entities

import org.jetbrains.exposed.sql.Column
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId : Column<Int> = integer("id").autoIncrement().primaryKey()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}

 */

object Notes : org.jetbrains.exposed.sql.Table() {
    val id: Column<Int> = integer("id").autoIncrement() //.primaryKey()
    val note = varchar("note", 256)
}

interface NoteEntity : Entity<NoteEntity> {
    companion object : Entity.Factory<NoteEntity>()
    val id: Int?
    var note: String
}

object NotesEntity: Table<NoteEntity>("notes") {
    val id = int("id").primaryKey()
    val note = varchar("note")
}