package ua.anadea.data.mapper

import ua.anadea.data.dto.AnadeaUserResponse
import ua.anadea.domain.data.User

fun AnadeaUserResponse.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = role,
    createdAt = createdAt,
    updatedAt = updatedAt
)