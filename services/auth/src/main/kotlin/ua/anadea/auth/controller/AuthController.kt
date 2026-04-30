package ua.anadea.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ua.anadea.auth.dto.AuthResponse
import ua.anadea.auth.dto.LoginRequest
import ua.anadea.auth.dto.RegisterRequest
import ua.anadea.auth.service.AuthService

@RestController
@RequestMapping("/api/v1/auth")
internal class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(request, response))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request, response))
    }

    @DeleteMapping("/user/{email}")
    fun deleteUser(@PathVariable email: String): ResponseEntity<Void> {
        authService.deleteUser(email)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.refresh(request, response))
    }
}
