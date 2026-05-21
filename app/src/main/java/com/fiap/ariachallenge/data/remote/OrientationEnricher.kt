package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.domain.analytics.AnalyticsMetricsCalculator
import com.fiap.ariachallenge.util.formatCurrencyCompact
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import java.time.LocalDateTime

object OrientationEnricher {

    fun enrich(orientation: Orientation, ideas: List<Idea>, projects: List<Project>): Orientation {
        val alignedIdeas = ideas.filter { it.category == orientation.category }
        val alignedProjects = projects.filter { it.originIdea.category == orientation.category }
        val approved = alignedIdeas.count {
            it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.EM_PROJETO
        }
        val progress = if (alignedIdeas.isEmpty()) {
            orientation.progress
        } else {
            (approved.toFloat() / alignedIdeas.size.toFloat()).coerceIn(0f, 1f)
        }
        val totalRoiReais = alignedProjects.sumOf { it.actualRoi ?: it.estimatedRoi }
        val roiDelta = AnalyticsMetricsCalculator.roiDeltaPercentForCategory(projects, orientation.category)

        return orientation.copy(
            ideasCount = alignedIdeas.size,
            ideasDelta = AnalyticsMetricsCalculator.ideasDeltaLast30Days(alignedIdeas),
            projectsActive = alignedProjects.count { it.status == ProjectStatus.EM_ANDAMENTO },
            roiCompact = formatCurrencyCompact(totalRoiReais),
            roiDeltaPercent = roiDelta,
            progress = progress,
        )
    }
}
