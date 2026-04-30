package ua.anadea.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ua.anadea.auth.entity.AuthUser

@Repository
interface AuthUserRepository : JpaRepository<AuthUser, String>
