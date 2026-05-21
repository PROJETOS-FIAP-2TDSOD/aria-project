package com.fiap.ariachallenge.domain.gamification

import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Project

object ProfileMetricsCalculator {

    fun calculateAttributedRoiReais(ideas: List<Idea>, projects: List<Project>, userId: String): Double {
        val userIdeas = ideas.filter { it.author.id == userId }
        val fromApprovedIdeas = userIdeas
            .filter { it.status == IdeaStatus.APROVADA }
            .sumOf { it.estimatedRoi ?: 0.0 }
        val fromProjects = projects
            .filter { it.originIdea.author.id == userId }
            .sumOf { it.actualRoi ?: it.estimatedRoi }
        return fromApprovedIdeas + fromProjects
    }

    fun calculateManagedRoiReais(projects: List<Project>): Double =
        projects.sumOf { it.actualRoi ?: it.estimatedRoi }

    fun calculateInProjectRoiReais(ideas: List<Idea>, projects: List<Project>, userId: String): Double {
        val userIdeasInProject = ideas
            .filter { it.author.id == userId && it.status == IdeaStatus.EM_PROJETO }
            .map { it.id }
            .toSet()
        return projects
            .filter { it.originIdea.author.id == userId && it.originIdea.id in userIdeasInProject }
            .sumOf { it.actualRoi ?: it.estimatedRoi }
    }
}
