package com.fiap.ariachallenge.data.security

import android.util.Base64
import com.fiap.ariachallenge.domain.model.User
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object MockJwtProvider {
    private const val ISSUER = "aria-mock"
    private const val TTL_SECONDS = 7 * 24 * 60 * 60L

    fun issue(user: User): String {
        val header = base64Url("""{"alg":"HS256","typ":"JWT"}""")
        val exp = System.currentTimeMillis() / 1000 + TTL_SECONDS
        val payloadJson = JSONObject()
            .put("sub", user.id)
            .put("email", user.email)
            .put("role", user.role.name)
            .put("iss", ISSUER)
            .put("exp", exp)
        val payload = base64Url(payloadJson.toString())
        val signature = base64Url("mock-${user.id}-${exp}")
        return "$header.$payload.$signature"
    }

    fun isValid(token: String?): Boolean {
        if (token.isNullOrBlank()) return false
        val parts = token.split(".")
        if (parts.size != 3) return false
        return runCatching {
            val json = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP), StandardCharsets.UTF_8)
            val exp = JSONObject(json).getLong("exp")
            val now = System.currentTimeMillis() / 1000
            exp > now
        }.getOrDefault(false)
    }

    private fun base64Url(value: String): String =
        Base64.encodeToString(value.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
            .trimEnd('=')
}
