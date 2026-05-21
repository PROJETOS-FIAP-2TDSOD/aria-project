package com.fiap.ariachallenge.ui.lider.dashboard

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.AiDashboardMonthInsight
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.analytics.AnalyticsMetricsCalculator
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CategoryShare(val category: IdeaCategory, val percent: Int)

data class TopProjectUi(
    val id: String,
    val title: String,
    val valueReais: Double,
    val maxReais: Double = 60_000.0,
)

data class FunnelStepUi(
    @StringRes val labelRes: Int,
    val count: Int,
    val widthPercent: Int,
    val convertPercent: String? = null,
)

data class AiInsightUi(@StringRes val eyebrowRes: Int, val body: String, val tone: AiInsightTone)

enum class AiInsightTone { Accent, Success, Info }

private data class MonthMetricDeltas(
    val ideasSubmittedDelta: Int,
    val approvalRateDelta: Int,
    val activeProjectsDelta: Int,
    val conversionDelta: Int,
)

data class DashboardLiderUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val selectedMonth: Int = LocalDate.now().monthValue,
    val availableMonthCount: Int = LocalDate.now().monthValue,
    val ideasSubmitted: Int = 0,
    val ideasSubmittedDelta: Int = 0,
    val approvalRatePercent: Int = 0,
    val approvalRateDelta: Int = 0,
    val activeProjects: Int = 0,
    val activeProjectsDelta: Int = 0,
    val conversionPercent: Int = 0,
    val conversionDelta: Int = 0,
    val roiAmount: Double = 0.0,
    val roiDeltaPercent: Int = 0,
    val roiSparkline: List<Float> = emptyList(),
    val roiDisplayAmount: Double = 0.0,
    val roiDisplayDeltaPercent: Int = 0,
    @StringRes val roiDisplayMonthRes: Int = R.string.month_jan,
    val monthlyRoiPoints: List<Float> = emptyList(),
    val monthlyMonthRes: List<Int> = emptyList(),
    val roiAccumulated: Double = 0.0,
    val categoryDistribution: List<CategoryShare> = emptyList(),
    val topProjects: List<TopProjectUi> = emptyList(),
    val funnel: List<FunnelStepUi> = emptyList(),
    val aiInsights: List<AiInsightUi> = emptyList(),
)

@HiltViewModel
class DashboardLiderViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val ideaRepository: IIdeaRepository,
    private val projectRepository: IProjectRepository,
    private val aiRepository: IAiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardLiderUiState())
    val uiState: StateFlow<DashboardLiderUiState> = _uiState.asStateFlow()
    private var monthInsightJob: Job? = null
    private var cachedIdeas: List<Idea> = emptyList()
    private var cachedProjects: List<Project> = emptyList()

    init { load() }

    fun refresh() = load()

    fun onMonthSelected(month: Int) {
        val clamped = month.coerceIn(1, currentMonthCount())
        if (clamped == _uiState.value.selectedMonth) return
        _uiState.update { current ->
            val withMonth = applyMonthSelection(current, clamped)
            if (cachedIdeas.isEmpty() && cachedProjects.isEmpty()) withMonth
            else withMonth.withMonthDeltas(cachedIdeas, cachedProjects, clamped)
        }
    }

    fun onMonthSelectionFinished(month: Int) {
        val clamped = month.coerceIn(1, currentMonthCount())
        monthInsightJob?.cancel()
        monthInsightJob = viewModelScope.launch {
            val monthInsight = aiRepository.getDashboardMonthInsight(clamped)
            _uiState.update { current ->
                if (current.selectedMonth != clamped) return@update current
                current.copy(
                    aiInsights = listOf(
                        AiInsightUi(R.string.dashboard_ai_forecast_eyebrow, monthInsight.forecastBody, AiInsightTone.Accent),
                        AiInsightUi(R.string.dashboard_ai_emerging_eyebrow, monthInsight.emergingBody, AiInsightTone.Success),
                    ),
                )
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.getCurrentUser().first()
                val ideas = ideaRepository.getAllIdeas().first()
                val projects = projectRepository.getAllProjects().first()
                val currentMonth = currentMonthCount()
                val month = _uiState.value.selectedMonth.coerceIn(1, currentMonth)
                val monthInsight = aiRepository.getDashboardMonthInsight(month)

                val submitted = ideas.size
                val approved = ideas.count { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.EM_PROJETO }
                val inProject = ideas.count { it.status == IdeaStatus.EM_PROJETO }
                val completed = projects.count { it.status == ProjectStatus.CONCLUIDO }
                val activeProjects = projects.count { it.status == ProjectStatus.EM_ANDAMENTO }
                val approvalRate = if (submitted == 0) 0 else (approved * 100) / submitted
                val conversion = if (submitted == 0) 0 else (inProject * 100) / submitted
                val totalRoi = projects.sumOf { (it.actualRoi ?: it.estimatedRoi) }

                val categoryGroups = ideas.groupBy { it.category }
                val categoryDistribution = categoryGroups.map { (cat, list) ->
                    CategoryShare(cat, if (submitted == 0) 0 else (list.size * 100) / submitted)
                }.sortedByDescending { it.percent }

                val topProjects = projects
                    .sortedByDescending { (it.actualRoi ?: it.estimatedRoi) }
                    .take(5)
                    .map { p ->
                        TopProjectUi(
                            id = p.id,
                            title = p.title,
                            valueReais = p.actualRoi ?: p.estimatedRoi,
                        )
                    }

                cachedIdeas = ideas
                cachedProjects = projects
                val year = LocalDate.now().year
                val monthlyRoi = AnalyticsMetricsCalculator.monthlyRoiSeries(projects, currentMonth, year)
                val monthDeltas = monthMetricDeltas(ideas, projects, month, year)
                val monthLabels = MONTH_LABEL_RES.take(currentMonth)
                val maxProjectReais = topProjects.maxOfOrNull { it.valueReais }?.coerceAtLeast(1.0) ?: 1.0
                val topWithScale = topProjects.map { it.copy(maxReais = maxProjectReais) }

                _uiState.update {
                    applyMonthSelection(
                        it.copy(
                        isLoading = false,
                        user = user,
                        availableMonthCount = currentMonth,
                        ideasSubmitted = submitted,
                        ideasSubmittedDelta = monthDeltas.ideasSubmittedDelta,
                        approvalRatePercent = approvalRate,
                        approvalRateDelta = monthDeltas.approvalRateDelta,
                        activeProjects = activeProjects,
                        activeProjectsDelta = monthDeltas.activeProjectsDelta,
                        conversionPercent = conversion,
                        conversionDelta = monthDeltas.conversionDelta,
                        roiAmount = totalRoi,
                        roiSparkline = monthlyRoi,
                        monthlyRoiPoints = monthlyRoi,
                        monthlyMonthRes = monthLabels,
                        roiAccumulated = totalRoi,
                        categoryDistribution = categoryDistribution,
                        topProjects = topWithScale,
                        funnel = listOf(
                            FunnelStepUi(R.string.dashboard_funnel_submitted, submitted, 100),
                            FunnelStepUi(
                                R.string.dashboard_funnel_approved,
                                approved,
                                pct(approved, submitted),
                                convertPct(approved, submitted),
                            ),
                            FunnelStepUi(
                                R.string.dashboard_funnel_in_project,
                                inProject,
                                pct(inProject, submitted),
                                convertPct(inProject, approved),
                            ),
                            FunnelStepUi(
                                R.string.dashboard_funnel_completed,
                                completed,
                                pct(completed, submitted),
                                convertPct(completed, inProject),
                            ),
                        ),
                        aiInsights = listOf(
                            AiInsightUi(R.string.dashboard_ai_forecast_eyebrow, monthInsight.forecastBody, AiInsightTone.Accent),
                            AiInsightUi(R.string.dashboard_ai_emerging_eyebrow, monthInsight.emergingBody, AiInsightTone.Success),
                        ),
                    ),
                        month,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun pct(part: Int, total: Int): Int =
        if (total <= 0) 0 else ((part * 100f) / total).toInt().coerceIn(0, 100)

    private fun convertPct(part: Int, total: Int): String? =
        if (total <= 0) null else "${((part * 100f) / total).toInt()}%"

    private fun currentMonthCount(): Int = LocalDate.now().monthValue

    private fun monthMetricDeltas(
        ideas: List<Idea>,
        projects: List<Project>,
        focusMonth: Int,
        year: Int,
    ): MonthMetricDeltas {
        val (prevYear, prevMonth) = previousCalendarMonth(year, focusMonth)
        val ideasThisMonth = AnalyticsMetricsCalculator.ideasCreatedInMonth(ideas, year, focusMonth)
        val ideasPrevMonth = AnalyticsMetricsCalculator.ideasCreatedInMonth(ideas, prevYear, prevMonth)
        val projectsThisMonth = AnalyticsMetricsCalculator.projectsStartedInMonth(projects, year, focusMonth)
        val projectsPrevMonth = AnalyticsMetricsCalculator.projectsStartedInMonth(projects, prevYear, prevMonth)
        return MonthMetricDeltas(
            ideasSubmittedDelta = AnalyticsMetricsCalculator.countDelta(
                ideasThisMonth.size,
                ideasPrevMonth.size,
            ),
            approvalRateDelta = AnalyticsMetricsCalculator.countDelta(
                AnalyticsMetricsCalculator.approvalRatePercent(ideasThisMonth),
                AnalyticsMetricsCalculator.approvalRatePercent(ideasPrevMonth),
            ),
            activeProjectsDelta = AnalyticsMetricsCalculator.countDelta(
                projectsThisMonth.count { it.status == ProjectStatus.EM_ANDAMENTO },
                projectsPrevMonth.count { it.status == ProjectStatus.EM_ANDAMENTO },
            ),
            conversionDelta = AnalyticsMetricsCalculator.countDelta(
                AnalyticsMetricsCalculator.conversionPercent(ideasThisMonth),
                AnalyticsMetricsCalculator.conversionPercent(ideasPrevMonth),
            ),
        )
    }

    private fun DashboardLiderUiState.withMonthDeltas(
        ideas: List<Idea>,
        projects: List<Project>,
        focusMonth: Int,
        year: Int = LocalDate.now().year,
    ): DashboardLiderUiState {
        val deltas = monthMetricDeltas(ideas, projects, focusMonth, year)
        return copy(
            ideasSubmittedDelta = deltas.ideasSubmittedDelta,
            approvalRateDelta = deltas.approvalRateDelta,
            activeProjectsDelta = deltas.activeProjectsDelta,
            conversionDelta = deltas.conversionDelta,
        )
    }

    private fun previousCalendarMonth(year: Int, month: Int): Pair<Int, Int> =
        if (month > 1) year to (month - 1) else (year - 1) to 12

    private fun applyMonthSelection(current: DashboardLiderUiState, month: Int): DashboardLiderUiState {
        val monthIdx = (month - 1).coerceIn(0, (current.availableMonthCount - 1).coerceAtLeast(0))
        val points = current.monthlyRoiPoints.ifEmpty { current.roiSparkline }
        val monthRoi = points.getOrNull(monthIdx)?.toDouble() ?: current.roiAmount
        val prevRoi = if (monthIdx > 0) points.getOrNull(monthIdx - 1)?.toDouble() else null
        val deltaPercent = when {
            prevRoi == null || prevRoi <= 0.0 -> 0
            else -> (((monthRoi - prevRoi) / prevRoi) * 100).toInt()
        }

        return current.copy(
            selectedMonth = month,
            roiDisplayMonthRes = current.monthlyMonthRes.getOrElse(monthIdx) {
                MONTH_LABEL_RES.getOrElse(monthIdx) { R.string.month_short_label }
            },
            roiDisplayAmount = monthRoi,
            roiDisplayDeltaPercent = deltaPercent,
            roiDeltaPercent = deltaPercent,
        )
    }

    companion object {
        internal val MONTH_LABEL_RES = listOf(
            R.string.month_jan, R.string.month_feb, R.string.month_mar, R.string.month_apr,
            R.string.month_may, R.string.month_jun, R.string.month_jul, R.string.month_aug,
            R.string.month_sep, R.string.month_oct, R.string.month_nov, R.string.month_dec,
        )
    }
}
