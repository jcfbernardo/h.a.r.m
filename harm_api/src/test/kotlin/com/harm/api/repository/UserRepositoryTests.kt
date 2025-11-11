package com.harm.api.repository

import com.harm.api.model.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories("com.harm.api.repository")
@EntityScan("com.harm.api.model")
@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `findByEmail deve retornar User quando email existe`() {
        val newUser = User(
            username = "Test User",
            email = "test@mail.com",
            passwordHash = "hashed_password_abc123"
        )

        entityManager.persistAndFlush(newUser)

        val foundUser = userRepository.findByEmail("test@mail.com")

        assertNotNull(foundUser)
        assertEquals(newUser.email, foundUser.email)
        assertEquals(newUser.username, foundUser.username)
    }

    @Test
    fun `findByEmail deve retornar null quando email nao existe`() {
        val foundUser = userRepository.findByEmail("fantasma@mail.com")

        assertNull(foundUser)
    }
}