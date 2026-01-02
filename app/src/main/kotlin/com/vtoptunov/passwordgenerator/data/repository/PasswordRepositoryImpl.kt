package com.vtoptunov.passwordgenerator.data.repository

import com.vtoptunov.passwordgenerator.data.local.dao.PasswordDao
import com.vtoptunov.passwordgenerator.data.local.entity.toDomain
import com.vtoptunov.passwordgenerator.data.local.entity.toEntity
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordRepositoryImpl @Inject constructor(
    private val passwordDao: PasswordDao
) : PasswordRepository {
    
    override fun getAllPasswords(): Flow<List<Password>> =
        passwordDao.getAllPasswordsFlow().map { it.map { entity -> entity.toDomain() } }
    
    override suspend fun getPasswordById(id: String): Password? =
        passwordDao.getPasswordById(id)?.toDomain()
    
    override suspend fun insertPassword(password: Password) =
        passwordDao.insertPassword(password.toEntity())
    
    override suspend fun deletePassword(password: Password) =
        passwordDao.deletePassword(password.toEntity())
    
    override suspend fun deletePasswordById(id: String) =
        passwordDao.deletePasswordById(id)
    
    override suspend fun getPasswordCount(): Int =
        passwordDao.getPasswordCount()
}
