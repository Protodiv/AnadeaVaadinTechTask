package ua.anadea.domain.data

data class AuthResponse(
    val email: String? = null,
    val accessToken: String? = null,
    val message: String? = null
)
