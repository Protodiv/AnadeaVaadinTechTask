package ua.anadea.user.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import ua.anadea.user.dto.ErrorResponse
import java.time.Instant


@RestControllerAdvice
internal class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(UserAppException::class)
    fun handleAppException(ex: UserAppException, request: WebRequest): ResponseEntity<ErrorResponse> {
        return buildResponse(ex.status, ex.message, request)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception occurred: {}", ex.message, ex)
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred: ${ex.message}",
            request
        )
    }

    private fun buildResponse(
        status: HttpStatus,
        message: String,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            title = status.name,
            status = status.value(),
            detail = message,
            instance = request.getDescription(false),
            timestamp = Instant.now()
        )
        return ResponseEntity(response, status)
    }
}
