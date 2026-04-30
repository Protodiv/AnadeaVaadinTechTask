package ua.anadea.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import ua.anadea.data.dto.AnadeaUserResponse
import ua.anadea.data.mapper.toDomain
import ua.anadea.domain.data.User
import ua.anadea.domain.repository.UserRepository

class UserRepositoryImpl(
    private val httpClient: HttpClient
) : UserRepository {

    override suspend fun getAllUsers(): List<User> {
        val response = httpClient.get("/api/v1/user/userList")
        return response.body<List<AnadeaUserResponse>>().map{ it.toDomain() }
    }

    override suspend fun getUser(id: String): User {
        val response = httpClient.get("/api/v1/user/user/$id")
        return response.body<AnadeaUserResponse>().toDomain()
    }

    override suspend fun getMe(): User {
        val response = httpClient.get("/api/v1/user/me")
        return response.body<AnadeaUserResponse>().toDomain()
    }
}
