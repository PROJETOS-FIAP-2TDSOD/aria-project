package com.fiap.ariachallenge.ui.gestor.criar_projeto

import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.ProjectMilestone
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

data class MilestoneFormItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val dueDateEpochMillis: Long? = null,
    val status: MilestoneStatus = MilestoneStatus.PENDING,
) {
    fun toDomain(): ProjectMilestone? {
        val dueMillis = dueDateEpochMillis ?: return null
        if (title.isBlank()) return null
        val dueDate = Instant.ofEpochMilli(dueMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        return ProjectMilestone(
            id = id,
            title = title.trim(),
            dueDate = dueDate,
            status = status,
        )
    }

    companion object {
        fun fromDomain(milestone: ProjectMilestone): MilestoneFormItem = MilestoneFormItem(
            id = milestone.id,
            title = milestone.title,
            dueDateEpochMillis = milestone.dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            status = milestone.status,
        )

        fun empty(): MilestoneFormItem = MilestoneFormItem()
    }
}

fun List<MilestoneFormItem>.toDomainMilestones(): List<ProjectMilestone> = mapNotNull { it.toDomain() }
