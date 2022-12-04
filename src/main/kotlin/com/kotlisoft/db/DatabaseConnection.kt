package com.kotlisoft.db

import org.ktorm.database.Database

object DatabaseConnection {
    val database = Database.connect(
        url = System.getenv("DATABASE_URI")
    )
}