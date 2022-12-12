package com.kotlisoft.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Notes : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val note = varchar("note", 256)
}