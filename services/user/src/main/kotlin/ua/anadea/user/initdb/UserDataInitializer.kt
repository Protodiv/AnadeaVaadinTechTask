package ua.anadea.user.initdb

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ua.anadea.user.entity.User
import ua.anadea.user.entity.UserRole
import ua.anadea.user.repository.UserRepository
import java.util.concurrent.atomic.AtomicBoolean

@Component
class UserDataInitializer(
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val initialized = AtomicBoolean(false)

    fun isInitialized(): Boolean = initialized.get()

    @EventListener(ApplicationReadyEvent::class)
    fun seadData() {
        try {
            logger.info("Checking if user database needs seeding...")
            if (userRepository.count() > 0L) {
                logger.info("User database already seeded. Skipping...")
                initialized.set(true)
                return
            }

            logger.info("Seeding user database with 500 test users...")
            val users = (1..500).map { i ->
                User(
                    name = "Test User $i",
                    email = "user$i@gmail.com",
                    role = if (i == 1) UserRole.ADMIN else UserRole.USER
                )
            }
            
            userRepository.saveAll(users)
            logger.info("Successfully seeded 500 test users (1 Admin, 499 Users).")
            initialized.set(true)
        } catch (e: Exception) {
            logger.error("Failed to seed user database: {}", e.message)
        }
    }
}
