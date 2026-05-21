package com.fiap.ariachallenge.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.fiap.ariachallenge.domain.model.Badge
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

@Singleton
class BadgeUnlockTracker @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(DATA_STORE_NAME) },
    )

    private val queue = ArrayDeque<Badge>()
    private val _currentCelebration = MutableStateFlow<Badge?>(null)
    val currentCelebration: StateFlow<Badge?> = _currentCelebration.asStateFlow()

    suspend fun syncBadges(userId: String, currentBadgeIds: List<String>) {
        val prefs = dataStore.data.first()
        val snapshotKey = snapshotKey(userId)
        val badgesKey = badgesKey(userId)
        val hadSnapshot = prefs[snapshotKey] == true
        val lastIds = prefs[badgesKey]?.split(SEPARATOR)?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
        val currentSet = currentBadgeIds.toSet()

        if (!hadSnapshot) {
            dataStore.edit {
                it[badgesKey] = currentBadgeIds.joinToString(SEPARATOR)
                it[snapshotKey] = true
            }
            return
        }

        val newlyUnlocked = currentBadgeIds.filter { it !in lastIds }
        dataStore.edit { it[badgesKey] = currentBadgeIds.joinToString(SEPARATOR) }

        newlyUnlocked.forEach { id ->
            Badge.fromId(id)?.let { queue.addLast(it) }
        }
        if (_currentCelebration.value == null) {
            _currentCelebration.value = queue.removeFirstOrNull()
        }
    }

    fun dismissCelebration() {
        _currentCelebration.value = queue.removeFirstOrNull()
    }

    suspend fun clearUser(userId: String) {
        dataStore.edit {
            it.remove(snapshotKey(userId))
            it.remove(badgesKey(userId))
        }
        queue.clear()
        _currentCelebration.value = null
    }

    private fun badgesKey(userId: String) = stringPreferencesKey("badges_$userId")
    private fun snapshotKey(userId: String) = booleanPreferencesKey("badges_snapshot_$userId")

    companion object {
        private const val DATA_STORE_NAME = "aria_badge_unlocks"
        private const val SEPARATOR = "|"
    }
}
