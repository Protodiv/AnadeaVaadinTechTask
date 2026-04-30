package ua.anadea.data.mapper

import ua.anadea.data.dto.AnadeaAuthResponse
import ua.anadea.domain.data.AuthResponse

fun AnadeaAuthResponse.toDomain() = AuthResponse(
    email = email,
    accessToken = accessToken,
    message = message
)