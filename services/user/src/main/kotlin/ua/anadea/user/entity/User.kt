package ua.anadea.user.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.*

enum class UserRole {
    USER, ADMIN
}

@Entity
@Table(name = "app_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole = UserRole.USER,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: OffsetDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime? = null
)
