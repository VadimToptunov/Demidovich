package com.vtoptunov.passwordgenerator.data.local.dao

import androidx.room.*
import com.vtoptunov.passwordgenerator.data.local.entity.PasswordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    
    @Query("SELECT * FROM passwords ORDER BY createdAt DESC")
    fun getAllPasswordsFlow(): Flow<List<PasswordEntity>>
    
    @Query("SELECT * FROM passwords WHERE id = :id")
    suspend fun getPasswordById(id: String): PasswordEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)
    
    @Delete
    suspend fun deletePassword(password: PasswordEntity)
    
    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: String)
    
    @Query("SELECT COUNT(*) FROM passwords")
    suspend fun getPasswordCount(): Int
}
