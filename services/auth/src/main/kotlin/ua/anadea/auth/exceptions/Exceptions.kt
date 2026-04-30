package ua.anadea.auth.exceptions

import org.springframework.http.HttpStatus

sealed class AppException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)

class ResourceNotFoundException(message: String) : AppException(HttpStatus.NOT_FOUND, message)
class UnauthorizedAccessException(message: String) : AppException(HttpStatus.UNAUTHORIZED, message)
class InternalServerException(message: String) : AppException(HttpStatus.INTERNAL_SERVER_ERROR, message)
class InvalidPageRequestException(message: String) : AppException(HttpStatus.BAD_REQUEST, message)
class UserAlreadyExistsException(message: String) : AppException(HttpStatus.CONFLICT, message)
class InvalidCredentialsException(message: String) : AppException(HttpStatus.UNAUTHORIZED, message)
