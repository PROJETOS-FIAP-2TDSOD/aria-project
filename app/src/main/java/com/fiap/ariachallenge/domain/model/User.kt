package com.fiap.ariachallenge.domain.model

import androidx.annotation.StringRes
import com.fiap.ariachallenge.R

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val department: String = "",
    val avatarInitials: String = name.take(2).uppercase(),
    val avatarLocalPath: String? = null,
    val totalIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val points: Int = 0,
    val badges: List<String> = emptyList(),
)

enum class UserRole(val displayName: String) {
    OPERADOR("Operador"),
    GESTOR("Gestor"),
    LIDER("Líder");

    @StringRes
    fun getDisplayNameRes(): Int = when (this) {
        OPERADOR -> R.string.user_role_operator
        GESTOR   -> R.string.user_role_manager
        LIDER    -> R.string.user_role_leader
    }
}
