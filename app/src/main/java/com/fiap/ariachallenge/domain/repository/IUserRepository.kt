package com.fiap.ariachallenge.domain.repository

import kotlinx.coroutines.flow.Flow
import com.fiap.ariachallenge.domain.model.Notification
import com.fiap.ariachallenge.domain.model.User

interface IUserRepository {
    fun getCurrentUser(): Flow<User>
    fun getProjectAssignableUsers(): Flow<List<User>>
    fun getUserById(id: String): Flow<User?>
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun markNotificationRead(notificationId: String): Result<Unit>
    suspend fun markAllNotificationsRead(userId: String): Result<Unit>
    fun calculateUserPoints(userId: String): Int
    fun calculateUserBadges(userId: String): List<String>
    suspend fun updateAvatarFromContentUri(contentUri: String): Result<Unit>
    suspend fun clearAvatar(): Result<Unit>
}
