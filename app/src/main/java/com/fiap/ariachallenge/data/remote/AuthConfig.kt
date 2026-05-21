package com.fiap.ariachallenge.data.remote

object AuthConfig {
    const val HEADER_AUTHORIZATION = "Authorization"
    const val BEARER_PREFIX = "Bearer "

    val PUBLIC_PATH_SUFFIXES = setOf(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
    )

    fun isPublicPath(path: String): Boolean =
        PUBLIC_PATH_SUFFIXES.any { path.endsWith(it) }
}
