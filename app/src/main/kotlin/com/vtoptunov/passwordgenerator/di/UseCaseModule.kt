package com.vtoptunov.passwordgenerator.di

import com.vtoptunov.passwordgenerator.domain.usecase.password.GeneratePasswordUseCase
import com.vtoptunov.passwordgenerator.domain.usecase.password.GeneratePasswordUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    
    @Binds
    @Singleton
    abstract fun bindGeneratePasswordUseCase(
        impl: GeneratePasswordUseCaseImpl
    ): GeneratePasswordUseCase
}

