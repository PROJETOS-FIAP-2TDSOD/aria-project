package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.mock.MockUsers
import com.fiap.ariachallenge.data.remote.dto.AuthResponseDto
import com.fiap.ariachallenge.data.remote.dto.IdeaDto
import com.fiap.ariachallenge.data.remote.dto.LoginRequestDto
import com.fiap.ariachallenge.data.remote.dto.OrientationDto
import com.fiap.ariachallenge.data.remote.dto.ProjectDto
import com.fiap.ariachallenge.data.remote.dto.RegisterRequestDto
import com.fiap.ariachallenge.data.security.MockJwtProvider
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AriaMockApiInterceptor @Inject constructor(
    private val store: InMemoryApiStore,
    private val authAccountRegistry: AuthAccountRegistry,
) : Interceptor {

    private val gson = Gson()
    private val jsonMedia = "application/json".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.trimEnd('/')
        val method = request.method
        val bodyString = request.body?.let { body ->
            val sink = okio.Buffer()
            body.writeTo(sink)
            sink.readUtf8()
        }.orEmpty()

        if (!AuthConfig.isPublicPath(path) && !hasValidBearer(request)) {
            return jsonResponse(request, 401, """{"error":"unauthorized"}""")
        }

        val (code, json) = when {
            path == "/api/v1/auth/login" && method == "POST" -> {
                val body = gson.fromJson(bodyString, LoginRequestDto::class.java)
                resolveLogin(body.email, body.password)?.let { user ->
                    200 to gson.toJson(buildAuthResponse(user))
                } ?: 401 to """{"error":"invalid_credentials"}"""
            }

            path == "/api/v1/auth/register" && method == "POST" -> {
                val body = gson.fromJson(bodyString, RegisterRequestDto::class.java)
                val email = body.email.trim().lowercase()
                when {
                    body.name.isBlank() || email.isBlank() || !email.contains("@") ->
                        400 to """{"error":"invalid_payload"}"""
                    emailExists(email) ->
                        409 to """{"error":"email_already_registered"}"""
                    else -> {
                        val user = User(
                            id = "u_${UUID.randomUUID()}",
                            name = body.name.trim(),
                            email = email,
                            role = runCatching { UserRole.valueOf(body.role) }.getOrDefault(UserRole.OPERADOR),
                            department = "Operações",
                            avatarInitials = body.name.trim().split(" ")
                                .mapNotNull { it.firstOrNull() }
                                .take(2)
                                .joinToString("")
                                .uppercase(),
                        )
                        authAccountRegistry.register(
                            StoredAuthAccount(email = email, password = body.password, user = user),
                        )
                        201 to gson.toJson(buildAuthResponse(user))
                    }
                }
            }

            path == "/api/v1/ideas" && method == "GET" ->
                200 to gson.toJson(store.ideas.value.map { it.toDto() })

            path == "/api/v1/ideas" && method == "POST" -> {
                val dto = gson.fromJson(bodyString, IdeaDto::class.java)
                val idea = dto.toDomain().copy(
                    id = dto.id.ifBlank { UUID.randomUUID().toString() },
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                )
                store.ideas.value = (store.ideas.value + idea).toMutableList()
                201 to gson.toJson(idea.toDto())
            }

            path.startsWith("/api/v1/ideas/") && method == "PUT" -> {
                val id = path.removePrefix("/api/v1/ideas/")
                val dto = gson.fromJson(bodyString, IdeaDto::class.java)
                val updated = dto.toDomain().copy(id = id, updatedAt = LocalDateTime.now())
                store.ideas.value = store.ideas.value.map { if (it.id == id) updated else it }.toMutableList()
                200 to gson.toJson(updated.toDto())
            }

            path.startsWith("/api/v1/ideas/") && method == "DELETE" -> {
                val id = path.removePrefix("/api/v1/ideas/")
                store.ideas.value = store.ideas.value.filter { it.id != id }.toMutableList()
                204 to ""
            }

            path == "/api/v1/projects" && method == "GET" ->
                200 to gson.toJson(store.projects.value.map { it.toDto() })

            path == "/api/v1/projects" && method == "POST" -> {
                val dto = gson.fromJson(bodyString, ProjectDto::class.java)
                val project = dto.toDomain().copy(
                    id = dto.id.ifBlank { UUID.randomUUID().toString() },
                    updatedAt = LocalDateTime.now(),
                )
                store.projects.value = (store.projects.value + project).toMutableList()
                201 to gson.toJson(project.toDto())
            }

            path.startsWith("/api/v1/projects/") && method == "PUT" -> {
                val id = path.removePrefix("/api/v1/projects/")
                val dto = gson.fromJson(bodyString, ProjectDto::class.java)
                val updated = dto.toDomain().copy(id = id, updatedAt = LocalDateTime.now())
                store.projects.value = store.projects.value.map { if (it.id == id) updated else it }.toMutableList()
                200 to gson.toJson(updated.toDto())
            }

            path.startsWith("/api/v1/projects/") && method == "DELETE" -> {
                val id = path.removePrefix("/api/v1/projects/")
                val exists = store.projects.value.any { it.id == id }
                if (!exists) {
                    404 to """{"error":"project_not_found"}"""
                } else {
                    store.projects.value = store.projects.value.filter { it.id != id }.toMutableList()
                    204 to ""
                }
            }

            path == "/api/v1/orientations" && method == "GET" -> {
                val enriched = store.orientations.value.map { orientation ->
                    OrientationEnricher.enrich(orientation, store.ideas.value, store.projects.value)
                }
                store.orientations.value = enriched.toMutableList()
                200 to gson.toJson(enriched.map { it.toDto() })
            }

            path == "/api/v1/orientations" && method == "POST" -> {
                val dto = gson.fromJson(bodyString, OrientationDto::class.java)
                val base = dto.toDomain().copy(
                    id = dto.id.ifBlank { UUID.randomUUID().toString() },
                    createdAt = LocalDateTime.now(),
                )
                val orientation = OrientationEnricher.enrich(base, store.ideas.value, store.projects.value)
                store.orientations.value = (store.orientations.value + orientation).toMutableList()
                201 to gson.toJson(orientation.toDto())
            }

            path.startsWith("/api/v1/orientations/") && method == "PUT" -> {
                val id = path.removePrefix("/api/v1/orientations/")
                val dto = gson.fromJson(bodyString, OrientationDto::class.java)
                val base = dto.toDomain().copy(id = id)
                val updated = OrientationEnricher.enrich(base, store.ideas.value, store.projects.value)
                store.orientations.value = store.orientations.value.map {
                    if (it.id == id) updated else it
                }.toMutableList()
                200 to gson.toJson(updated.toDto())
            }

            path.startsWith("/api/v1/orientations/") && method == "DELETE" -> {
                val id = path.removePrefix("/api/v1/orientations/")
                store.orientations.value = store.orientations.value.filter { it.id != id }.toMutableList()
                204 to ""
            }

            else -> 404 to """{"error":"not found"}"""
        }

        if (shouldPersist(method, path, code)) {
            store.persistSnapshot()
        }

        return jsonResponse(request, code, json)
    }

    private fun jsonResponse(request: okhttp3.Request, code: Int, json: String): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code in 200..299) "OK" else "Error")
            .body(json.toResponseBody(jsonMedia))
            .build()

    private fun hasValidBearer(request: okhttp3.Request): Boolean {
        val header = request.header(AuthConfig.HEADER_AUTHORIZATION) ?: return false
        if (!header.startsWith(AuthConfig.BEARER_PREFIX, ignoreCase = true)) return false
        val token = header.removePrefix(AuthConfig.BEARER_PREFIX).trim()
        return MockJwtProvider.isValid(token)
    }

    private fun emailExists(email: String): Boolean {
        val normalized = email.trim().lowercase()
        return authAccountRegistry.emailExists(normalized) ||
            MockUsers.loginCredentials.containsKey(normalized)
    }

    private fun resolveLogin(email: String, password: String): User? {
        val normalized = email.trim().lowercase()
        authAccountRegistry.find(normalized)?.let { account ->
            return if (account.password == password) account.user else null
        }
        val credentials = MockUsers.loginCredentials[normalized] ?: return null
        return if (credentials.first == password) credentials.second else null
    }

    private fun shouldPersist(method: String, path: String, code: Int): Boolean {
        if (code !in 200..299 && code != 204) return false
        if (method !in setOf("POST", "PUT", "DELETE")) return false
        return path.startsWith("/api/v1/ideas") ||
            path.startsWith("/api/v1/projects") ||
            path.startsWith("/api/v1/orientations")
    }

    private fun buildAuthResponse(user: User): AuthResponseDto {
        val token = MockJwtProvider.issue(user)
        return AuthResponseDto(
            accessToken = token,
            expiresInSeconds = 7 * 24 * 60 * 60,
            user = user.toDto(),
        )
    }
}
