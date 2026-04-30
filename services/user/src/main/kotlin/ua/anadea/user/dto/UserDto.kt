package ua.anadea.user.dto

import ua.anadea.user.entity.UserRole
import java.time.OffsetDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)

data class CreateUserRequest(
    val name: String,
    val password: String,
    val email: String,
    val role: UserRole
)

data class UpdateUserRequest(
    val name: String,
    val email: String,
    val role: UserRole
)
