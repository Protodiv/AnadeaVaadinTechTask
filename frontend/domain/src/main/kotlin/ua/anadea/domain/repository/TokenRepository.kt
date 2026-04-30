package ua.anadea.domain.repository

interface TokenRepository {

    fun saveBearerToken(token: String)

    fun getBearerToken(): String?
}