package ua.anadea.data.dto

import kotlinx.serialization.Serializable
import ua.anadea.data.util.OffsetDateTimeSerializer
import ua.anadea.domain.data.UserRole
import java.time.OffsetDateTime

@Serializable
data class AnadeaUserResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val updatedAt: OffsetDateTime? = null,
)
