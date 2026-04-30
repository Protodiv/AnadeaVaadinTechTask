package ua.anadea.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import ua.anadea.auth.entity.AuthUserRole

data class LoginRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank val password: String
)

data class RegisterRequest(
    @field:NotBlank val name: String,
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank val password: String,
    val role: AuthUserRole = AuthUserRole.USER
)

data class AuthResponse(
    val email: String,
    val accessToken: String? = null,
    val message: String? = null
)
