package com.demidovich.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demidovich.domain.model.Password
import com.demidovich.domain.model.PasswordCategory

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

// Mapper extensions
fun PasswordEntity.toDomain(): Password {
    return Password(
        id = id,
        password = password,
        category = PasswordCategory.valueOf(category),
        createdAt = createdAt,
        lastModified = lastModified,
        isFavorite = isFavorite,
        note = note
    )
}

fun Password.toEntity(): PasswordEntity {
    return PasswordEntity(
        id = id,
        password = password,
        category = category.name,
        createdAt = createdAt,
        lastModified = lastModified,
        isFavorite = isFavorite,
        note = note
    )
}

