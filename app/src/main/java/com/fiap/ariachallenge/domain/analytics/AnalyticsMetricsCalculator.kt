package com.fiap.ariachallenge.domain.analytics

import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

object AnalyticsMetricsCalculator {

    fun ideasCreatedInMonth(ideas: List<Idea>, year: Int, month: Int): List<Idea> =
        ideas.filter { it.createdAt.year == year && it.createdAt.monthValue == month }

    fun projectsStartedInMonth(projects: List<Project>, year: Int, month: Int): List<Project> =
        projects.filter { it.startDate.year == year && it.startDate.monthValue == month }

    fun monthlyRoiSeries(projects: List<Project>, monthCount: Int, year: Int = LocalDate.now().year): List<Float> {
        if (monthCount <= 0) return emptyList()
        return (1..monthCount).map { month ->
            projectsStartedInMonth(projects, year, month)
                .sumOf { it.actualRoi ?: it.estimatedRoi }
                .toFloat()
        }
    }

    fun countDelta(current: Int, previous: Int): Int = current - previous

    fun percentDelta(current: Int, previous: Int): Int = when {
        previous <= 0 -> if (current <= 0) 0 else 100
        else -> (((current - previous).toFloat() / previous) * 100f).roundToInt()
    }

    fun approvalRatePercent(ideas: List<Idea>): Int {
        if (ideas.isEmpty()) return 0
        val approved = ideas.count { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.EM_PROJETO }
        return (approved * 100) / ideas.size
    }

    fun conversionPercent(ideas: List<Idea>): Int {
        if (ideas.isEmpty()) return 0
        val inProject = ideas.count { it.status == IdeaStatus.EM_PROJETO }
        return (inProject * 100) / ideas.size
    }

    fun totalRoiReais(projects: List<Project>): Double =
        projects.sumOf { it.actualRoi ?: it.estimatedRoi }

    fun averagePendingDays(ideas: List<Idea>): Double? {
        val pending = ideas.filter {
            it.status == IdeaStatus.AGUARDANDO_ANALISE || it.status == IdeaStatus.EM_ANALISE
        }
        if (pending.isEmpty()) return null
        val now = LocalDateTime.now()
        val days = pending.map { ChronoUnit.DAYS.between(it.createdAt.toLocalDate(), now.toLocalDate()) }
        return days.average()
    }

    fun formatDaysShort(days: Double): String {
        val rounded = (days * 10).roundToInt() / 10.0
        return if (rounded % 1.0 == 0.0) "${rounded.toInt()}d" else "${rounded}d"
    }

    fun categorySharePercents(ideas: List<Idea>): List<Pair<IdeaCategory, Int>> {
        if (ideas.isEmpty()) return emptyList()
        val total = ideas.size
        return ideas.groupBy { it.category }
            .map { (category, list) -> category to (list.size * 100 / total) }
            .sortedByDescending { it.second }
    }

    fun roiDeltaPercentForCategory(projects: List<Project>, category: IdeaCategory): Int {
        val aligned = projects.filter { it.originIdea.category == category }
        val now = LocalDateTime.now()
        val recentRoi = aligned
            .filter { it.startDate.isAfter(now.minusDays(30)) }
            .sumOf { it.actualRoi ?: it.estimatedRoi }
        val olderRoi = aligned
            .filter { it.startDate.isBefore(now.minusDays(30)) }
            .sumOf { it.actualRoi ?: it.estimatedRoi }
        return percentDelta(recentRoi.toInt(), olderRoi.toInt())
    }

    fun ideasDeltaLast30Days(ideas: List<Idea>): Int {
        val cutoff = LocalDateTime.now().minusDays(30)
        return ideas.count { it.createdAt.isAfter(cutoff) }
    }

    fun activeProjectsCount(projects: List<Project>): Int =
        projects.count { it.status == ProjectStatus.EM_ANDAMENTO }
}
