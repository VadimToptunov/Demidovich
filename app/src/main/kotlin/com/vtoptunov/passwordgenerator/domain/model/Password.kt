package com.vtoptunov.passwordgenerator.domain.model

import java.util.UUID

data class Password(
    val id: String = UUID.randomUUID().toString(),
    val password: String,
    val category: PasswordCategory = PasswordCategory.UNCATEGORIZED,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val note: String? = null
)

enum class PasswordCategory(val displayName: String) {
    UNCATEGORIZED("Uncategorized"),
    WORK("Work"),
    PERSONAL("Personal"),
    BANKING("Banking"),
    SOCIAL("Social Media"),
    EMAIL("Email"),
    SHOPPING("Shopping"),
    OTHER("Other")
}
