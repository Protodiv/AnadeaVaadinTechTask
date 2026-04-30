package ua.anadea.auth.entity

import jakarta.persistence.*

enum class AuthUserRole {
    USER, ADMIN
}

@Entity
@Table(name = "auth_user")
data class AuthUser(
    @Id
    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: AuthUserRole = AuthUserRole.USER
)
