package ua.anadea.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ua.anadea.data.dto.AnadeaUserResponse
import ua.anadea.data.dto.CreateUserRequest
import ua.anadea.data.dto.UpdateUserRequest
import ua.anadea.data.mapper.toDomain
import ua.anadea.domain.data.User
import ua.anadea.domain.data.UserRole
import ua.anadea.domain.repository.AdminRepository
import java.rmi.ServerException

class AdminRepositoryImpl(
    private val httpClient: HttpClient
) : AdminRepository {

    override suspend fun createUser(name: String, email: String, password: String, role: UserRole): User {
        val response = httpClient.post("/api/v1/user/admin/user") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateUserRequest(
                    name = name,
                    password = password,
                    email = email,
                    role = role
                )
            )
        }
        if (!response.status.isSuccess())
            throw ServerException(response.body<String>())
        return response.body<AnadeaUserResponse>().toDomain()
    }

    override suspend fun updateUser(user: User): User {
        val response = httpClient.put("/api/v1/user/admin/user/${user.id}") {
            contentType(ContentType.Application.Json)
            setBody(
                    UpdateUserRequest(
                    name = user.name,
                    email = user.email,
                    role = user.role
                )
            )
        }
        if (!response.status.isSuccess())
            throw ServerException(response.body<String>())
        return response.body<AnadeaUserResponse>().toDomain()
    }

    override suspend fun deleteUser(id: String) {
       val response = httpClient.delete("/api/v1/user/admin/user/$id")
        if (!response.status.isSuccess())
            throw ServerException(response.body<String>())
    }
}
