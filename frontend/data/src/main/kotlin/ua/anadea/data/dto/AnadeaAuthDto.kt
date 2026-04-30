package ua.anadea.data.dto

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class AnadeaAuthResponse(
    val email: String? = null,
    val accessToken: String? = null,
    val message: String? = null
)
@Serializable
data class AnadeaAuthErrorResponse(
    val type: String? = "about:blank",
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String,
    val timestamp: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)