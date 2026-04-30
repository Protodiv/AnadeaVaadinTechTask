package ua.anadea.domain.repository

import ua.anadea.domain.data.AuthResponse
import ua.anadea.domain.data.UserRole

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResponse
    suspend fun register(name: String, email: String, password: String, role: UserRole): AuthResponse
    suspend fun refresh(): AuthResponse
}
