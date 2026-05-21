package com.fiap.ariachallenge.domain.model

import androidx.annotation.StringRes
import com.fiap.ariachallenge.R
import java.time.LocalDateTime

data class Orientation(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val author: User,
    val category: IdeaCategory,
    val priority: OrientationPriority,
    val period: String,
    val targetRoles: List<UserRole>,
    val keyMetrics: List<OrientationKeyMetric> = emptyList(),
    val ideasCount: Int = 0,
    val ideasDelta: Int = 0,
    val projectsActive: Int = 0,
    val roiCompact: String = "0k",
    val roiDeltaPercent: Int = 0,
    val progress: Float = 0f,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
)

enum class OrientationPriority(val displayName: String) {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta"),
    CRITICA("Crítica");

    @StringRes
    fun getDisplayNameRes(): Int = when (this) {
        BAIXA   -> R.string.orientation_priority_low
        MEDIA   -> R.string.orientation_priority_medium
        ALTA    -> R.string.orientation_priority_high
        CRITICA -> R.string.orientation_priority_critical
    }
}
