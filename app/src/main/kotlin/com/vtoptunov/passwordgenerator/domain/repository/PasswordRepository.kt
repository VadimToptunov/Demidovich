package com.vtoptunov.passwordgenerator.domain.repository

import com.vtoptunov.passwordgenerator.domain.model.Password
import kotlinx.coroutines.flow.Flow

interface PasswordRepository {
    fun getAllPasswords(): Flow<List<Password>>
    suspend fun getPasswordById(id: String): Password?
    suspend fun insertPassword(password: Password)
    suspend fun deletePassword(password: Password)
    suspend fun deletePasswordById(id: String)
    suspend fun getPasswordCount(): Int
}
