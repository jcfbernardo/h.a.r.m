package com.harm.api.dto

data class RegisterRequestDTO(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequestDTO(
    val email: String,
    val password: String
)

data class JwtResponseDTO(
    val username: String,
    val email: String,
    val token: String?
)