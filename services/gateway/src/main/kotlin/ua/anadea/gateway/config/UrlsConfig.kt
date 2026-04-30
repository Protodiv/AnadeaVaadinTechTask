package ua.anadea.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "urls")
data class UrlsConfig(
    val authServiceUrl: String,
    val userServiceUrl: String
)