package com.fiap.ariachallenge.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import com.fiap.ariachallenge.data.local.AvatarStorage
import com.fiap.ariachallenge.data.session.AuthSessionManager
import com.fiap.ariachallenge.data.mock.MockNotifications
import com.fiap.ariachallenge.data.mock.MockUsers
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
class FakeUserRepository @Inject constructor(
    private val authSessionManager: AuthSessionManager,
    private val avatarStorage: AvatarStorage,
    private val store: InMemoryApiStore,
) : IUserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    private val _notifications = MutableStateFlow(MockNotifications.allNotifications.toMutableList())

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

    override fun getProjectAssignableUsers(): Flow<List<User>> = flow {
        emit(MockUsers.allUsers.filter { it.role != UserRole.LIDER })
    }

    override fun getUserById(id: String): Flow<User?> = flow {
        delay(400)
        emit(MockUsers.getById(id))
    }

    override fun getNotifications(userId: String): Flow<List<Notification>> = _notifications

    override suspend fun markNotificationRead(notificationId: String): Result<Unit> {
        delay(300)
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }.toMutableList()
        return Result.success(Unit)
    }

    override suspend fun markAllNotificationsRead(userId: String): Result<Unit> {
        delay(500)
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }.toMutableList()
        return Result.success(Unit)
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
