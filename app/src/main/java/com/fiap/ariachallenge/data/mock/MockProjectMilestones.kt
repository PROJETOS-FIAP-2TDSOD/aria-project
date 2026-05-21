package com.fiap.ariachallenge.data.mock

import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.ProjectMilestone
import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import java.time.LocalDate

object MockProjectMilestones {

    fun defaultRoadmap(baseDate: LocalDate = LocalDate.now()): List<ProjectMilestone> = listOf(
        ProjectMilestone(
            id = "ms-1",
            title = "Validação técnica",
            dueDate = baseDate.plusMonths(1),
            status = MilestoneStatus.COMPLETED,
        ),
        ProjectMilestone(
            id = "ms-2",
            title = "Piloto",
            dueDate = baseDate.plusMonths(2),
            status = MilestoneStatus.COMPLETED,
        ),
        ProjectMilestone(
            id = "ms-3",
            title = "Lançamento regional",
            dueDate = baseDate.plusMonths(4),
            status = MilestoneStatus.IN_PROGRESS,
        ),
        ProjectMilestone(
            id = "ms-4",
            title = "Rollout nacional",
            dueDate = baseDate.plusMonths(6),
            status = MilestoneStatus.PENDING,
        ),
    )

    fun teamFor(vararg members: ProjectTeamMember): List<ProjectTeamMember> = members.toList()
}
