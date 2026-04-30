package ua.anadea.auth.dto

import ua.anadea.auth.entity.AuthUserRole

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: AuthUserRole
)