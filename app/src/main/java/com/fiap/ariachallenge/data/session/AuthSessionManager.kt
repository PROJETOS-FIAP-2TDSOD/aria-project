package com.fiap.ariachallenge.data.session

import com.fiap.ariachallenge.data.local.AuthTokenStore
import com.fiap.ariachallenge.data.local.UserSessionStore
import com.fiap.ariachallenge.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManager @Inject constructor(
    private val authTokenStore: AuthTokenStore,
    private val userSessionStore: UserSessionStore,
) {
    suspend fun persist(user: User, accessToken: String) {
        authTokenStore.saveAccessToken(accessToken)
        userSessionStore.saveUser(user)
    }

    suspend fun restoreUser(): User? {
        if (!authTokenStore.hasValidToken()) {
            clear()
            return null
        }
        return userSessionStore.readUser()
    }

    suspend fun getAccessToken(): String? = authTokenStore.readAccessToken()

    suspend fun clear() {
        authTokenStore.clear()
        userSessionStore.clear()
    }
}
