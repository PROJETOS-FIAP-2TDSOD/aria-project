package com.fiap.ariachallenge.domain.repository

import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole = UserRole.OPERADOR,
    ): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun recoverPassword(email: String): Result<Unit>
}
