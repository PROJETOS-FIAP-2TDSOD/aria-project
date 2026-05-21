package com.fiap.ariachallenge.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.fiap.ariachallenge.data.remote.StoredAuthAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class AuthAccountStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(DATA_STORE_NAME) },
    )

    suspend fun readAll(): List<StoredAuthAccount> {
        val json = dataStore.data.first()[ACCOUNTS_JSON].orEmpty()
        return AuthAccountJson.decodeAll(json)
    }

    suspend fun saveAll(accounts: Collection<StoredAuthAccount>) {
        val json = AuthAccountJson.encodeAll(accounts)
        dataStore.edit { prefs ->
            if (accounts.isEmpty()) {
                prefs.remove(ACCOUNTS_JSON)
            } else {
                prefs[ACCOUNTS_JSON] = json
            }
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "aria_auth_accounts"
        private val ACCOUNTS_JSON = stringPreferencesKey("accounts_json")
    }
}
