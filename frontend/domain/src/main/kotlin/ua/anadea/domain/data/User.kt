package ua.anadea.domain.data

import java.time.OffsetDateTime

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)
