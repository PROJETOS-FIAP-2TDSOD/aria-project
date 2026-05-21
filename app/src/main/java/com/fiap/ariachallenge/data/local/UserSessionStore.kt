package com.fiap.ariachallenge.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import com.fiap.ariachallenge.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(DATA_STORE_NAME) },
    )

    suspend fun readUser(): User? {
        val json = dataStore.data.first()[USER_JSON] ?: return null
        return UserSessionJson.decode(json)
    }

    suspend fun saveUser(user: User) {
        val json = UserSessionJson.encode(user) ?: return
        dataStore.edit { it[USER_JSON] = json }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(USER_JSON) }
    }

    companion object {
        private const val DATA_STORE_NAME = "aria_user_session"
        private val USER_JSON = stringPreferencesKey("user_json")
    }
}
