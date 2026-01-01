package com.demidovich.data.local.dao

import androidx.room.*
import com.demidovich.data.local.entity.PasswordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    
    @Query("SELECT * FROM passwords ORDER BY createdAt DESC")
    fun getAllPasswordsFlow(): Flow<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: String): PasswordEntity?
    
    @Query("SELECT * FROM passwords WHERE category = :category ORDER BY createdAt DESC")
    fun getPasswordsByCategory(category: String): Flow<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePasswords(): Flow<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE password LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%'")
    fun searchPasswords(query: String): Flow<List<PasswordEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasswords(passwords: List<PasswordEntity>)
    
    @Update
    suspend fun updatePassword(password: PasswordEntity)
    
    @Delete
    suspend fun deletePassword(password: PasswordEntity)
    
    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: String)
    
    @Query("DELETE FROM passwords")
    suspend fun deleteAllPasswords()
    
    @Query("SELECT COUNT(*) FROM passwords")
    suspend fun getPasswordCount(): Int
    
    @Query("SELECT COUNT(*) FROM passwords WHERE category = :category")
    suspend fun getPasswordCountByCategory(category: String): Int
    
    @Query("SELECT password, COUNT(*) as count FROM passwords GROUP BY password HAVING count > 1")
    suspend fun getReusedPasswords(): List<ReusedPasswordInfo>
}

data class ReusedPasswordInfo(
    val password: String,
    val count: Int
)

