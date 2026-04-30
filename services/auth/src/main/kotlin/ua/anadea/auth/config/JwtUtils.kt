package ua.anadea.auth.config

import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.util.*

@Component
class JwtUtils(
    private val keyPair: KeyPair,
    private val jwtConfig: JwtConfig
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val parser = Jwts
        .parser()
        .verifyWith(keyPair.public)
        .build()

    fun generateToken(username: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.accessTokenExpiry)

        return Jwts.builder()
            .subject(username)
            .claim("role", "ROLE_$role") // Standard prefix for Spring Security
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(keyPair.private)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.refreshTokenExpiry)

        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(keyPair.private)
            .compact()
    }

    fun validateToken(authToken: String): Boolean {
        try {
            parser.parseSignedClaims(authToken)
            return true
        } catch (e: Exception) {
            logger.error("Invalid JWT signature: {}", e.message,e)
        }
        return false
    }

    fun getUserIdFromJWT(token: String): String {
        val claims = parser
            .parseSignedClaims(token)
            .payload

        return claims.subject
    }
}
