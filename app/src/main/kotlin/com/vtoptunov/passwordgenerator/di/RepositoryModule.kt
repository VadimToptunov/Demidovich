package com.vtoptunov.passwordgenerator.di

import com.vtoptunov.passwordgenerator.data.repository.PasswordRepositoryImpl
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindPasswordRepository(impl: PasswordRepositoryImpl): PasswordRepository
}
