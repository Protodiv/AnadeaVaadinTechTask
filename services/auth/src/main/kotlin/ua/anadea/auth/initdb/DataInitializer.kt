package ua.anadea.auth.initdb

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ua.anadea.auth.entity.AuthUser
import ua.anadea.auth.entity.AuthUserRole
import ua.anadea.auth.repository.AuthUserRepository
import java.util.concurrent.atomic.AtomicBoolean

@Component
class DataInitializer(
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val initialized = AtomicBoolean(false)

    fun isInitialized(): Boolean = initialized.get()

    @EventListener(ApplicationReadyEvent::class)
    fun seadData() {
        try {
            if (authUserRepository.count() > 0) {
                logger.info("Database already contains data. Skipping seeding...")
                initialized.set(true)
                return
            }
        } catch (e: Exception) {
            logger.warn("Could not check for existing data, the table might not be ready: {}", e.message)
            return
        }

        val users = (1..500).mapNotNull { i ->
            passwordEncoder.encode("pass$i")?.let { passwordHash ->
                AuthUser(
                    email = "user$i@gmail.com",
                    passwordHash = passwordHash,
                    role = if (i == 1) AuthUserRole.ADMIN else AuthUserRole.USER
                )
            }
        }

        try {
            authUserRepository.saveAll(users)
            logger.info("Successfully seeded 500 test users (1 Admin, 499 Users).")
            initialized.set(true)
        } catch (e: Exception) {
            logger.error("Failed to seed database: {}", e.message)
        }
    }
}
