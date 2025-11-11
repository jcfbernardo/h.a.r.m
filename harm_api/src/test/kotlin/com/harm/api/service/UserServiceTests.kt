package com.harm.api.service

import com.harm.api.dto.RegisterRequestDTO
import com.harm.api.model.User
import com.harm.api.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockKExtension::class)
class UserServiceTests {

    @MockK
    private lateinit var userRepository: UserRepository
    @MockK
    private lateinit var passwordEncoder: PasswordEncoder
    @InjectMockKs
    private lateinit var userService: UserService

    @Test
    fun `register() should register a user when email doesnt exists`() {
        val dto = RegisterRequestDTO("Test User", "test@mail.com", "password123")
        val hashedPassword = "hashed_password_abc123"
        val userSlot = slot<User>()

        every { userRepository.findByEmail("test@mail.com") } returns null
        every { passwordEncoder.encode("password123") } returns hashedPassword
        every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }
        userService.register(dto)

        verify(exactly = 1) { userRepository.findByEmail("test@mail.com")  }
        verify(exactly = 1) { passwordEncoder.encode("password123")  }
        verify(exactly = 1) { userRepository.save(any())  }
    }

    @Test
    fun `UserService throw exception and verify if controller captures and returns 400 Bad Request`() {
        val dto = RegisterRequestDTO("Test User", "test@mail.com", "password123")

        val existentUser = User(id = 1L, email = "test@mail.com", username = "Outro User", passwordHash = "abc")

        every { userRepository.findByEmail(dto.email) } returns existentUser

        val exception = assertThrows<IllegalStateException> {
            userService.register(dto)
        }

        assertEquals("Email is already registered.", exception.message)

        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `Simulate valid auth and check response equals to 200 OK and contains JWT dto`() {
        val email = "test@mail.com"
        val userFromDB = User(
            id = 1L,
            email = email,
            username = "Test User",
            passwordHash = "hashed_password_abc123"
        )

        every { userRepository.findByEmail(email) } returns userFromDB

        val userDetails = userService.loadUserByUsername(email)

        verify(exactly = 1) { userRepository.findByEmail(email) }

        assertEquals(userFromDB.email, userDetails.username)
        assertEquals(userFromDB.passwordHash, userDetails.password)
        assertTrue(userDetails.authorities.isEmpty())
    }

    @Test
    fun `Simulate AuthenticationManager throw AuthenticationException and check if response equals 401 Unauthorized`() {
        val email = "fantasma@mail.com"

        every { userRepository.findByEmail(email) } returns null

        val exception = assertThrows<UsernameNotFoundException> {
            userService.loadUserByUsername(email)
        }

        assertEquals("User not found with email: $email", exception.message)
    }
}