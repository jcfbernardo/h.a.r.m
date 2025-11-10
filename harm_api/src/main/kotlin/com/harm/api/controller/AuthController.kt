package com.harm.api.controller

import com.harm.api.dto.JwtResponseDTO
import com.harm.api.dto.LoginRequestDTO
import com.harm.api.dto.RegisterRequestDTO
import com.harm.api.security.JwtUtils
import com.harm.api.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val userService: UserService,
    val authenticationManager: AuthenticationManager,
    val jwtUtils: JwtUtils
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody registerRequestDTO: RegisterRequestDTO): ResponseEntity<String> {
        return try {
            userService.register(registerRequestDTO)
            ResponseEntity.ok("User successfully registered.")
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequestDTO: LoginRequestDTO): ResponseEntity<*> {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequestDTO.email, loginRequestDTO.password)
            )

            val userDetails = authentication.principal as UserDetails

            val token = jwtUtils.generateToken(userDetails.username)

            val user = userService.findByEmail(userDetails.username)
                ?: throw IllegalStateException("User not found.")

            return ResponseEntity.ok(
                JwtResponseDTO(
                    token = token,
                    email = user.email,
                    username = user.username,
                )
            )
        } catch (e: AuthenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password invalid.")
        }
    }
}