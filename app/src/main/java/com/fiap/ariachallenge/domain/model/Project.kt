package com.fiap.ariachallenge.domain.model

import androidx.annotation.StringRes
import com.fiap.ariachallenge.R
import java.time.LocalDateTime

data class Project(
    val id: String,
    val title: String,
    val description: String,
    val originIdea: Idea,
    val manager: User,
    val status: ProjectStatus,
    val progress: Int = 0,
    val estimatedRoi: Double = 0.0,
    val actualRoi: Double? = null,
    val budget: Double = 0.0,
    val sponsorLabel: String = "",
    val strategicOrientationLabel: String = "",
    val teamMembers: List<ProjectTeamMember> = emptyList(),
    val milestones: List<ProjectMilestone> = emptyList(),
    val startDate: LocalDateTime = LocalDateTime.now(),
    val expectedEndDate: LocalDateTime = LocalDateTime.now().plusMonths(3),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ProjectStatus(val displayName: String) {
    PLANEJAMENTO("Planejamento"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    SUSPENSO("Suspenso"),
    CANCELADO("Cancelado");

    @StringRes
    fun getDisplayNameRes(): Int = when (this) {
        PLANEJAMENTO -> R.string.project_status_planning
        EM_ANDAMENTO -> R.string.project_status_in_progress
        CONCLUIDO    -> R.string.project_status_completed
        SUSPENSO     -> R.string.project_status_suspended
        CANCELADO    -> R.string.project_status_cancelled
    }
}
