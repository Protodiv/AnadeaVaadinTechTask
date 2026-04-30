package ua.anadea.domain.repository

import ua.anadea.domain.data.User
import java.util.*

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun getUser(id: String): User
    suspend fun getMe(): User
}
