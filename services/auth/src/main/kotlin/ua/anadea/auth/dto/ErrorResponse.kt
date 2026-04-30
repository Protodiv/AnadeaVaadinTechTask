package ua.anadea.auth.dto

import java.time.Instant

data class ErrorResponse(
    val type: String? = "about:blank",
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String,
    val timestamp: Instant = Instant.now()
)
