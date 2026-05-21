package com.fiap.ariachallenge.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "OPERADOR",
)

data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long = 0,
    val user: UserDto,
)
