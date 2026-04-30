package ua.anadea.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtConfig(
    val privateKeyPath: String,
    val publicKeyPath: String,
    val accessTokenExpiry: Long,
    var refreshTokenExpiry: Long
)
