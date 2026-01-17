package com.vtoptunov.passwordgenerator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.model.PasswordCategory

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey
    val id: String,
    val password: String,
    val category: String,
    val createdAt: Long,
    val lastModified: Long,
    val isFavorite: Boolean,
    val note: String?
)

fun PasswordEntity.toDomain(): Password = Password(
    id = id,
    password = password,
    category = try {
        PasswordCategory.valueOf(category)
    } catch (e: IllegalArgumentException) {
        PasswordCategory.UNCATEGORIZED
    },
    createdAt = createdAt,
    lastModified = lastModified,
    isFavorite = isFavorite,
    note = note
)

fun Password.toEntity(): PasswordEntity = PasswordEntity(
    id = id,
    password = password,
    category = category.name,
    createdAt = createdAt,
    lastModified = lastModified,
    isFavorite = isFavorite,
    note = note
)
