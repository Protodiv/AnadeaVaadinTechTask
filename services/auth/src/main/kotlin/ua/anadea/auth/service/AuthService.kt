package ua.anadea.auth.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ua.anadea.auth.config.JwtConfig
import ua.anadea.auth.config.JwtUtils
import ua.anadea.auth.dto.AuthResponse
import ua.anadea.auth.dto.LoginRequest
import ua.anadea.auth.dto.RegisterRequest
import ua.anadea.auth.entity.AuthUser
import ua.anadea.auth.exceptions.InternalServerException
import ua.anadea.auth.exceptions.InvalidCredentialsException
import ua.anadea.auth.exceptions.ResourceNotFoundException
import ua.anadea.auth.exceptions.UserAlreadyExistsException
import ua.anadea.auth.repository.AuthUserRepository
import kotlin.jvm.optionals.getOrElse

@Service
class AuthService(
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val jwtConfig: JwtConfig,
    private val userServiceClient: UserServiceClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun login(request: LoginRequest, response: HttpServletResponse): AuthResponse {
        val user = authUserRepository.findById(request.email).getOrElse {
            throw InvalidCredentialsException("Invalid email: ${request.email}")
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw InvalidCredentialsException("Invalid password")
        }

        val accessToken = jwtUtils.generateToken(user.email, user.role.name)
        val refreshToken = jwtUtils.generateRefreshToken(user.email)

        setRefreshTokenCookie(response, refreshToken)

        return AuthResponse(
            email = user.email,
            accessToken = accessToken,
            message = "Login successful"
        )
    }

    @Transactional
    fun register(request: RegisterRequest, response: HttpServletResponse): AuthResponse {
        if (authUserRepository.existsById(request.email)) {
            throw UserAlreadyExistsException("User with email ${request.email} already exists")
        }
        
        val passwordHash = passwordEncoder.encode(request.password)
            ?: throw InternalServerException("Failed to encode password")
        
        val newUser = AuthUser(
            email = request.email,
            passwordHash = passwordHash,
            role = request.role
        )
        
        authUserRepository.save(newUser)

        try {
            userServiceClient.createUser(request.name, request.email, request.password, request.role)
        } catch (e: Exception) {
            logger.error("Failed to create user profile in user-service for email: {}. Rolling back auth user creation.", request.email, e)
            throw InternalServerException("User profile creation failed: ${e.message}")
        }

        val accessToken = jwtUtils.generateToken(newUser.email, newUser.role.name)
        val refreshToken = jwtUtils.generateRefreshToken(newUser.email)

        setRefreshTokenCookie(response, refreshToken)

        return AuthResponse(
            email = newUser.email,
            accessToken = accessToken,
            message = "Registration successful"
        )
    }

    @Transactional
    fun deleteUser(email: String) {
        if (!authUserRepository.existsById(email)) {
            throw InvalidCredentialsException("User not found: $email")
        }

        authUserRepository.deleteById(email)
        logger.info("Deleted auth user with email: {}", email)
    }

    @Transactional(readOnly = true)
    fun refresh(request: HttpServletRequest, response: HttpServletResponse): AuthResponse {
        val refreshToken = request.cookies?.find { it.name == "refreshToken" }?.value
            ?: throw InvalidCredentialsException("No refresh token provided.")

        if (!jwtUtils.validateToken(refreshToken)) {
            throw InvalidCredentialsException("Invalid refresh token")
        }

        val email = jwtUtils.getUserIdFromJWT(refreshToken)
        val user = authUserRepository.findById(email)
            .orElseThrow { InvalidCredentialsException("User not found") }

        val newAccessToken = jwtUtils.generateToken(user.email, user.role.name)
        val newRefreshToken = jwtUtils.generateRefreshToken(user.email)

        setRefreshTokenCookie(response, newRefreshToken)

        return AuthResponse(
            email = user.email,
            accessToken = newAccessToken,
            message = "Token refreshed successfully"
        )
    }

    private fun setRefreshTokenCookie(response: HttpServletResponse, refreshToken: String) {
        val cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true) 
            .path("/")
            .maxAge(jwtConfig.refreshTokenExpiry / 1000)
            .sameSite("Strict")
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}
