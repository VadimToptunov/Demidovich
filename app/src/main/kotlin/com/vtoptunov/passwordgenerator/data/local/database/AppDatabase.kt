package com.vtoptunov.passwordgenerator.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vtoptunov.passwordgenerator.data.local.dao.PasswordDao
import com.vtoptunov.passwordgenerator.data.local.entity.PasswordEntity

@Database(
    entities = [PasswordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
    
    companion object {
        const val DATABASE_NAME = "password_generator_db"
    }
}

