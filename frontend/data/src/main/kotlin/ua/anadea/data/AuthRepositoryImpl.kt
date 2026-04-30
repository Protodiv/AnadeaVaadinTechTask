package ua.anadea.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import ua.anadea.data.dto.AnadeaAuthErrorResponse
import ua.anadea.data.mapper.toDomain
import ua.anadea.data.dto.AnadeaAuthResponse
import ua.anadea.data.dto.LoginRequest
import ua.anadea.data.dto.RegisterRequest
import ua.anadea.data.token.TokenManager
import ua.anadea.domain.data.AuthResponse
import ua.anadea.domain.data.UserRole
import ua.anadea.domain.repository.AuthRepository
import ua.anadea.domain.repository.TokenRepository

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val tokenRepository: TokenRepository,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResponse = executeAndSave {
        httpClient.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
        }
    }

    override suspend fun register(name: String, email: String, password: String, role: UserRole): AuthResponse = executeAndSave {
        httpClient.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    role = role.name
                )
            )
        }
    }

    override suspend fun refresh(): AuthResponse {
        val response = httpClient.post("/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
        }
        return when(response.status){
            HttpStatusCode.OK -> {
                val body = response.body<AnadeaAuthResponse>()
                body.accessToken?.let {
                    tokenRepository.saveBearerToken(it)
                }
                body.toDomain()
            }
            else -> {
                val body = response.body<AnadeaAuthErrorResponse>()
                throw Exception(body.detail)
            }
        }
    }

    private suspend fun executeAndSave(block: suspend () -> HttpResponse): AuthResponse {
        val response = block()

        return when(response.status){
            HttpStatusCode.OK -> {
                val body = response.body<AnadeaAuthResponse>()
                body.accessToken?.let {
                    tokenRepository.saveBearerToken(it)
                }
                body.toDomain()
            }
            else -> {
                val body = response.body<AnadeaAuthErrorResponse>()
                throw Exception(body.detail)
            }
        }
    }
}
