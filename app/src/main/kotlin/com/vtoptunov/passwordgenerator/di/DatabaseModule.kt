package com.vtoptunov.passwordgenerator.di

import android.content.Context
import androidx.room.Room
import com.vtoptunov.passwordgenerator.data.local.dao.PasswordDao
import com.vtoptunov.passwordgenerator.data.local.database.AppDatabase
import com.vtoptunov.passwordgenerator.data.security.KeystoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideKeystoreManager(@ApplicationContext context: Context): KeystoreManager {
        return KeystoreManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager
    ): AppDatabase {
        // BUG FIX #1: Use secure passphrase from Android Keystore
        // instead of hardcoded string visible in source code
        val passphrase = keystoreManager.getDatabasePassphrase()
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
    fun providePasswordDao(database: AppDatabase): PasswordDao =
        database.passwordDao()
}
