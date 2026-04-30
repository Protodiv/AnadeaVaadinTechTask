package ua.anadea.user.mapper

import ua.anadea.user.dto.UserResponse
import ua.anadea.user.entity.User

fun User.toResponse() = UserResponse(
    id = this.id!!,
    name = this.name,
    email = this.email,
    role = this.role,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
