package ua.anadea.user.service

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ua.anadea.user.dto.UserResponse
import ua.anadea.user.exceptions.ResourceNotFoundException
import ua.anadea.user.mapper.toResponse
import ua.anadea.user.repository.UserRepository
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getUserById(id: UUID): UserResponse {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
            .toResponse()
    }

    @Transactional(readOnly = true)
    fun getMyUser(jwt: Jwt): UserResponse {
        val email = jwt.subject
        return userRepository.findByEmail(email)
            ?.toResponse()
            ?: throw ResourceNotFoundException("User not found with email: $email")
    }
}
