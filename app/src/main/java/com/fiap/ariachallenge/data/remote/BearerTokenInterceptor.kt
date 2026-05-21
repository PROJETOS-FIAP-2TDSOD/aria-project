package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.local.AuthTokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BearerTokenInterceptor @Inject constructor(
    private val authTokenStore: AuthTokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.trimEnd('/')
        if (AuthConfig.isPublicPath(path)) {
            return chain.proceed(request)
        }

        val token = authTokenStore.cachedAccessToken
        val authedRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .header(AuthConfig.HEADER_AUTHORIZATION, "${AuthConfig.BEARER_PREFIX}$token")
                .build()
        } else {
            request
        }
        return chain.proceed(authedRequest)
    }
}
