package com.demidovich.di

import android.content.Context
import androidx.room.Room
import com.demidovich.data.local.dao.PasswordDao
import com.demidovich.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        // Generate a secure passphrase for SQLCipher
        // In production, use Android Keystore to securely store this
        val passphrase = SQLiteDatabase.getBytes("your-secure-passphrase-here".toCharArray())
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun providePasswordDao(database: AppDatabase): PasswordDao {
        return database.passwordDao()
    }
}

