package ua.anadea.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ua.anadea.user.dto.CreateUserRequest
import ua.anadea.user.dto.UpdateUserRequest
import ua.anadea.user.dto.UserResponse
import ua.anadea.user.entity.User
import ua.anadea.user.exceptions.ResourceNotFoundException
import ua.anadea.user.mapper.toResponse
import ua.anadea.user.repository.UserRepository
import java.util.*

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val authServiceClient: AuthServiceClient
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        val user = User(
            name = request.name,
            email = request.email,
            role = request.role
        )
//        TODO(ADD ::: Proper CreateAuthUser with Kafka)
//        authServiceClient.createAuthUser(request.name,request.email, request.password, request.role)
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun updateUser(id: UUID, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
            
        val updatedUser = user.copy(
            name = request.name,
            email = request.email,
            role = request.role
        )
        return userRepository.save(updatedUser).toResponse()
    }

    @Transactional
    fun deleteUser(id: UUID) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User not found with id: $id")
        }

        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        authServiceClient.deleteAuthUser(user.email)
        userRepository.deleteById(id)
    }
}
