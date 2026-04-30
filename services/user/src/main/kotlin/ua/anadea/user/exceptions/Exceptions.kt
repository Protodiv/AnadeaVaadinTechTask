package ua.anadea.user.exceptions

import org.springframework.http.HttpStatus

sealed class UserAppException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)

class ResourceNotFoundException(message: String) : UserAppException(HttpStatus.NOT_FOUND, message)