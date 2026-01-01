package com.demidovich.data.repository

import com.demidovich.data.local.dao.PasswordDao
import com.demidovich.data.local.entity.toDomain
import com.demidovich.data.local.entity.toEntity
import com.demidovich.domain.model.Password
import com.demidovich.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordRepositoryImpl @Inject constructor(
    private val passwordDao: PasswordDao
) : PasswordRepository {
    
    override fun getAllPasswords(): Flow<List<Password>> {
        return passwordDao.getAllPasswordsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getPasswordsByCategory(category: String): Flow<List<Password>> {
        return passwordDao.getPasswordsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getFavoritePasswords(): Flow<List<Password>> {
        return passwordDao.getFavoritePasswords().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchPasswords(query: String): Flow<List<Password>> {
        return passwordDao.searchPasswords(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getPasswordById(id: String): Password? {
        return passwordDao.getPasswordById(id)?.toDomain()
    }
    
    override suspend fun insertPassword(password: Password) {
        passwordDao.insertPassword(password.toEntity())
    }
    
    override suspend fun insertPasswords(passwords: List<Password>) {
        passwordDao.insertPasswords(passwords.map { it.toEntity() })
    }
    
    override suspend fun updatePassword(password: Password) {
        passwordDao.updatePassword(password.toEntity())
    }
    
    override suspend fun deletePassword(password: Password) {
        passwordDao.deletePassword(password.toEntity())
    }
    
    override suspend fun deletePasswordById(id: String) {
        passwordDao.deletePasswordById(id)
    }
    
    override suspend fun deleteAllPasswords() {
        passwordDao.deleteAllPasswords()
    }
    
    override suspend fun getPasswordCount(): Int {
        return passwordDao.getPasswordCount()
    }
}

