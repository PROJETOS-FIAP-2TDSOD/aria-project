package com.fiap.ariachallenge.domain.model

import java.time.LocalDate

enum class MilestoneStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
}

data class ProjectMilestone(
    val id: String,
    val title: String,
    val dueDate: LocalDate,
    val status: MilestoneStatus = MilestoneStatus.PENDING,
)
