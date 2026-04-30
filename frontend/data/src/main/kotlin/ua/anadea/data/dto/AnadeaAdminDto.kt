package ua.anadea.data.dto

import kotlinx.serialization.Serializable
import ua.anadea.domain.data.UserRole

@Serializable
data class CreateUserRequest(
    val name: String,
    val password: String,
    val email: String,
    val role: UserRole
)

@Serializable
data class UpdateUserRequest(
    val name: String,
    val email: String,
    val role: UserRole
)