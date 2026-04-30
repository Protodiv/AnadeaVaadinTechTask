package ua.anadea.domain.repository

import ua.anadea.domain.data.User
import ua.anadea.domain.data.UserRole

interface AdminRepository {
    suspend fun createUser(name: String, email: String, password: String, role: UserRole): User
    suspend fun updateUser(user: User): User
    suspend fun deleteUser(id: String)
}