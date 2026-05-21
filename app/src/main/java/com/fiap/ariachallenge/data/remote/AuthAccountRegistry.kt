package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.local.AuthAccountStore
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class StoredAuthAccount(
    val email: String,
    val password: String,
    val user: com.fiap.ariachallenge.domain.model.User,
)

@Singleton
class AuthAccountRegistry @Inject constructor(
    private val authAccountStore: AuthAccountStore,
) {
    private val registered = ConcurrentHashMap<String, StoredAuthAccount>()
    private val loadMutex = Mutex()
    @Volatile
    private var loaded = false

    private suspend fun ensureLoaded() {
        if (loaded) return
        loadMutex.withLock {
            if (loaded) return
            authAccountStore.readAll().forEach { account ->
                registered[account.email] = account
            }
            loaded = true
        }
    }

    fun register(account: StoredAuthAccount) = runBlocking(Dispatchers.IO) {
        registerSuspend(account)
    }

    suspend fun registerSuspend(account: StoredAuthAccount) {
        ensureLoaded()
        val normalized = account.copy(email = account.email.trim().lowercase())
        registered[normalized.email] = normalized
        authAccountStore.saveAll(registered.values)
    }

    fun find(email: String): StoredAuthAccount? = runBlocking(Dispatchers.IO) {
        ensureLoaded()
        registered[email.trim().lowercase()]
    }

    fun emailExists(email: String): Boolean = runBlocking(Dispatchers.IO) {
        ensureLoaded()
        registered.containsKey(email.trim().lowercase())
    }
}
