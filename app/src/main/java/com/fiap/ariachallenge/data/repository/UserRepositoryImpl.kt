package com.fiap.ariachallenge.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import com.fiap.ariachallenge.data.local.AvatarStorage
import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.session.AuthSessionManager
import com.fiap.ariachallenge.data.remote.InMemoryApiStore
import com.fiap.ariachallenge.domain.gamification.GamificationCalculator
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.Notification
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authSessionManager: AuthSessionManager,
    private val avatarStorage: AvatarStorage,
    private val store: InMemoryApiStore,
    private val api: AriaApiService,
) : IUserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    suspend fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    suspend fun clearCurrentUser() {
        _currentUser.value = null
    }

    suspend fun restoreSessionIfNeeded(): User? {
        _currentUser.value?.let { return it }
        val stored = authSessionManager.restoreUser() ?: return null
        _currentUser.value = stored
        return stored
    }

    override fun getCurrentUser(): Flow<User> = _currentUser.filterNotNull()

    // GET /users/me - atualiza a sessao com pontos/badges/contadores frescos do
    // backend. Quem ja observa getCurrentUser() continuamente (ex: tela de perfil)
    // recebe o valor novo automaticamente, ja que os dois compartilham _currentUser.
    override suspend fun refreshCurrentUser(): Result<User> = runCatching {
        val fresh = api.getCurrentUserProfile().toDomain()
        val existing = _currentUser.value
        val merged = if (existing != null) fresh.copy(avatarLocalPath = existing.avatarLocalPath) else fresh
        persistUser(merged)
        merged
    }

    override fun getProjectAssignableUsers(): Flow<List<User>> = flow {
        emit(api.getUsers().map { it.toDomain() }.filter { it.role != UserRole.LIDER })
    }.catch { emit(emptyList()) }

    override fun getUserById(id: String): Flow<User?> = flow {
        emit(runCatching { api.getUserById(id).toDomain() }.getOrNull())
    }.catch { emit(null) }

    override fun getNotifications(userId: String): Flow<List<Notification>> = flow {
        emit(api.getNotifications().map { it.toDomain() })
    }.catch { emit(emptyList()) }

    override suspend fun markNotificationRead(notificationId: String): Result<Unit> = runCatching {
        api.markNotificationAsRead(notificationId)
        Unit
    }

    override suspend fun markAllNotificationsRead(userId: String): Result<Unit> = runCatching {
        api.markAllNotificationsAsRead()
    }

    override fun calculateUserPoints(userId: String): Int {
        val ideas = ideasByAuthor(userId)
        return GamificationCalculator.calculatePoints(ideas)
    }

    override fun calculateUserBadges(userId: String): List<String> {
        val ideas = ideasByAuthor(userId)
        return GamificationCalculator.calculateBadgeIds(ideas)
    }

    private fun ideasByAuthor(userId: String): List<Idea> =
        store.ideas.value.filter { it.author.id == userId }

    override suspend fun updateAvatarFromContentUri(contentUri: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(IllegalStateException("No user session"))
        return avatarStorage.saveFromContentUri(user.id, contentUri, user.avatarLocalPath).mapCatching { localPath ->
            persistUser(user.copy(avatarLocalPath = localPath))
        }
    }

    override suspend fun clearAvatar(): Result<Unit> = runCatching {
        val user = _currentUser.value ?: error("No user session")
        avatarStorage.deleteFile(user.avatarLocalPath)
        persistUser(user.copy(avatarLocalPath = null))
    }

    private suspend fun persistUser(user: User) {
        _currentUser.value = user
        val token = authSessionManager.getAccessToken()
        if (token != null) {
            authSessionManager.persist(user, token)
        }
    }
}