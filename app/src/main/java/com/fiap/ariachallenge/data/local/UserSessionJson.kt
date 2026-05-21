package com.fiap.ariachallenge.data.local

import org.json.JSONObject
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole

object UserSessionJson {
    fun encode(user: User): String? =
        runCatching {
            JSONObject()
                .put("id", user.id)
                .put("name", user.name)
                .put("email", user.email)
                .put("role", user.role.name)
                .put("department", user.department)
                .put("avatarInitials", user.avatarInitials)
                .put("avatarLocalPath", user.avatarLocalPath.orEmpty())
                .put("totalIdeas", user.totalIdeas)
                .put("approvedIdeas", user.approvedIdeas)
                .toString()
        }.getOrNull()

    fun decode(json: String): User? =
        runCatching {
            val o = JSONObject(json)
            User(
                id = o.getString("id"),
                name = o.getString("name"),
                email = o.getString("email"),
                role = UserRole.valueOf(o.getString("role")),
                department = o.optString("department", ""),
                avatarInitials = o.optString(
                    "avatarInitials",
                    o.getString("name").take(2).uppercase()
                ),
                avatarLocalPath = o.optString("avatarLocalPath").takeIf { it.isNotBlank() },
                totalIdeas = o.optInt("totalIdeas", 0),
                approvedIdeas = o.optInt("approvedIdeas", 0),
            )
        }.getOrNull()
}
