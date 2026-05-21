package com.fiap.ariachallenge.ui.gestor.criar_projeto

import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import com.fiap.ariachallenge.domain.model.User
import java.util.UUID

data class TeamMemberFormItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val roleIndex: Int = 0,
) {
    fun toDomain(usersById: Map<String, User>, roleLabels: List<String>): ProjectTeamMember? {
        val user = usersById[userId] ?: return null
        val role = roleLabels.getOrNull(roleIndex) ?: roleLabels.firstOrNull().orEmpty()
        return ProjectTeamMember(user = user, projectRole = role)
    }

    companion object {
        fun fromDomain(member: ProjectTeamMember, roleLabels: List<String>): TeamMemberFormItem {
            val roleIndex = roleLabels.indexOf(member.projectRole).let { idx ->
                if (idx >= 0) idx else 0
            }
            return TeamMemberFormItem(
                id = "${member.user.id}-$roleIndex",
                userId = member.user.id,
                roleIndex = roleIndex,
            )
        }

        fun empty(): TeamMemberFormItem = TeamMemberFormItem()
    }
}

fun List<TeamMemberFormItem>.toDomainTeamMembers(
    usersById: Map<String, User>,
    roleLabels: List<String>,
): List<ProjectTeamMember> = mapNotNull { it.toDomain(usersById, roleLabels) }
