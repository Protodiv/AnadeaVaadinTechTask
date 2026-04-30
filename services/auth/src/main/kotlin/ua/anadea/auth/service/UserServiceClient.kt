package ua.anadea.auth.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import ua.anadea.auth.config.JwtUtils
import ua.anadea.auth.dto.CreateUserRequest
import ua.anadea.auth.entity.AuthUserRole
import ua.anadea.auth.exceptions.InternalServerException

@Service
class UserServiceClient(
    @Value("\${services.user-service.url}")
    private val userServiceBaseUrl: String,
    private val jwtUtils: JwtUtils
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val restClient = RestClient.builder()
        .baseUrl(userServiceBaseUrl)
        .build()

    fun createUser(name: String, email: String, password: String, role: AuthUserRole) {
        val requestBody = CreateUserRequest(name, email, password, role)
        
        val systemToken = jwtUtils.generateToken("auth-service-system", AuthUserRole.ADMIN.name)
        
        logger.info("Calling user-service admin endpoint to create user: {}", email)

        val response = restClient.post()
            .uri("/api/v1/user/admin/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $systemToken")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .onStatus({ it.isError }, { _, resp ->
                throw InternalServerException("User creation in user-service failed. ${resp.statusCode}, ${String(resp.body.readBytes())}")
            })
            .toBodilessEntity()

        logger.info("User creation in user-service successful. Status: {}", response.statusCode)
    }
}
