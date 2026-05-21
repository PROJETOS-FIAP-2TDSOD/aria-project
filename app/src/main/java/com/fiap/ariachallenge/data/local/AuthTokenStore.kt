package com.fiap.ariachallenge.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.fiap.ariachallenge.data.security.MockJwtProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class AuthTokenStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(DATA_STORE_NAME) },
    )

    @Volatile
    var cachedAccessToken: String? = null

    suspend fun saveAccessToken(token: String) {
        cachedAccessToken = token
        dataStore.edit {
            it[ACCESS_TOKEN] = token
        }
    }

    suspend fun readAccessToken(): String? {
        val token = dataStore.data.first()[ACCESS_TOKEN]
        if (token == null || !MockJwtProvider.isValid(token)) {
            cachedAccessToken = null
            return null
        }
        cachedAccessToken = token
        return token
    }

    suspend fun hasValidToken(): Boolean = readAccessToken() != null

    suspend fun clear() {
        cachedAccessToken = null
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(TOKEN_EXPIRES_AT)
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "aria_auth_tokens"
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val TOKEN_EXPIRES_AT = longPreferencesKey("token_expires_at")
    }
}
