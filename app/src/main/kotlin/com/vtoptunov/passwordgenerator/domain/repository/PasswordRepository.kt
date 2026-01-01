package com.vtoptunov.passwordgenerator.domain.repository

import com.vtoptunov.passwordgenerator.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    fun getAllPasswords(): Flow<List<Password>>
    fun getPasswordsByCategory(category: String): Flow<List<Password>>
    fun getFavoritePasswords(): Flow<List<Password>>
    fun searchPasswords(query: String): Flow<List<Password>>
    suspend fun getPasswordById(id: String): Password?
    suspend fun insertPassword(password: Password)
    suspend fun insertPasswords(passwords: List<Password>)
    suspend fun updatePassword(password: Password)
    suspend fun deletePassword(password: Password)
    suspend fun deletePasswordById(id: String)
    suspend fun deleteAllPasswords()
    suspend fun getPasswordCount(): Int
}

