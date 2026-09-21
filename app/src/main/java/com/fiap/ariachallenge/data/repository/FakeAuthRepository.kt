package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.dto.LoginRequestDto
import com.fiap.ariachallenge.data.remote.dto.RegisterRequestDto
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.local.BadgeUnlockTracker
import com.fiap.ariachallenge.data.session.AuthSessionManager
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import retrofit2.HttpException

@Singleton
class FakeAuthRepository @Inject constructor(
    private val api: AriaApiService,
    private val authSessionManager: AuthSessionManager,
    private val userRepository: FakeUserRepository,
    private val badgeUnlockTracker: BadgeUnlockTracker,
) : IAuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        delay(400)
        val response = api.login(
            LoginRequestDto(email = email.trim().lowercase(), password = password),
        )
        val user = response.user.toDomain()
        authSessionManager.persist(user, response.accessToken)
        userRepository.setCurrentUser(user)
        user
    }.recoverCatching { error ->
        if (error is HttpException && error.code() == 401) {
            throw Exception("ERR_INVALID_CREDENTIALS")
        }
        throw error
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
    ): Result<User> = runCatching {
        delay(500)
        val response = api.register(
            RegisterRequestDto(
                name = name.trim(),
                email = email.trim().lowercase(),
                password = password,
                role = role.name,
            ),
        )
        val user = response.user.toDomain()
        authSessionManager.persist(user, response.accessToken)
        userRepository.setCurrentUser(user)
        user
    }.recoverCatching { error ->
        when {
            error is HttpException && error.code() == 409 ->
                throw Exception("ERR_EMAIL_EXISTS")
            error is HttpException && error.code() == 400 ->
                throw Exception("ERR_INVALID")
            else -> throw error
        }
    }

    override suspend fun logout() {
        delay(200)
        val userId = authSessionManager.restoreUser()?.id
        authSessionManager.clear()
        userRepository.clearCurrentUser()
        userId?.let { badgeUnlockTracker.clearUser(it) }
    }

    override suspend fun getCurrentUser(): User? {
        authSessionManager.getAccessToken()
        val user = authSessionManager.restoreUser() ?: return null
        userRepository.setCurrentUser(user)
        return user
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        delay(1200)
        return if (email.contains("@")) Result.success(Unit)
        else Result.failure(Exception("ERR_INVALID_EMAIL"))
    }
}
