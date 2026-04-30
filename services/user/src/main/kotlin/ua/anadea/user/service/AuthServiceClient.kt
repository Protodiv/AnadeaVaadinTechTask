package ua.anadea.user.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import ua.anadea.user.entity.UserRole
import ua.anadea.user.exceptions.ResourceNotFoundException

@Service
class AuthServiceClient(
    @Value("\${services.auth-service.url}")
    private val authServiceBaseUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val restClient = RestClient.builder()
        .baseUrl(authServiceBaseUrl)
        .build()
    
    fun createAuthUser(name: String, email: String, password: String, role: UserRole){
        logger.info("Calling auth-service to create auth user: {}", email)

        val requestBody = mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "role" to role.name
        )

        restClient.post()
            .uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .onStatus({ it.isError }, { _, resp ->
                throw ResourceNotFoundException("Auth user creation failed: ${resp.statusCode}")
            })
            .toBodilessEntity()
    }
    
    fun deleteAuthUser(email: String){
        logger.info("Calling auth-service to delete auth user: {}", email)

        restClient.delete()
            .uri("/api/v1/auth/user/{email}", email)
            .retrieve()
            .onStatus({ it.isError }, { _, resp ->
                throw ResourceNotFoundException("Auth user deletion failed: ${resp.statusCode}")
            })
            .toBodilessEntity()
    }
}