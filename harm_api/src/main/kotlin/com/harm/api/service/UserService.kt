package com.harm.api.service

import com.harm.api.dto.RegisterRequestDTO
import com.harm.api.model.User
import com.harm.api.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Collections.emptyList

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val foundUser =
            userRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found with email: $email")
        return SpringUser(foundUser.email, foundUser.passwordHash, emptyList())
    }

    fun register(registerRequest: RegisterRequestDTO) {
        val emailExists = userRepository.findByEmail(registerRequest.email)
            when(emailExists == null) {
                false -> throw IllegalStateException("Email is already registered.")
                true -> {
                    val newUser = User(
                        email = registerRequest.email,
                        passwordHash = passwordEncoder.encode(registerRequest.password),
                        username = registerRequest.username
                    )
                    userRepository.save(newUser)}
            }
    }

    fun findByEmail(email: String) : User? {
        return userRepository.findByEmail(email)
    }
}