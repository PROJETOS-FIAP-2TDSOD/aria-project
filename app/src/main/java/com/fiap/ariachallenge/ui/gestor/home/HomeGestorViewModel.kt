package com.fiap.ariachallenge.ui.gestor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.AiTextInsight
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.analytics.AnalyticsMetricsCalculator
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.util.formatCurrencyCompact
import java.time.LocalDate
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestorMetrics(
    val pendingIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0,
    val inReviewCount: Int = 0,
    val approvalRatePercent: Int? = null,
    val totalRoiLabel: String? = null,
    val roiMonthDeltaPercent: Int? = null,
    val conversionPercent: Int? = null,
    val conversionMonthDelta: Int? = null,
    val avgPendingDaysLabel: String? = null,
    val activeProjectsDelta: Int? = null,
)

data class HomeGestorUiState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val metrics: GestorMetrics = GestorMetrics(),
    val pendingIdeas: List<Idea> = emptyList(),
    val activeProjects: List<Project> = emptyList(),
    val orientations: List<Orientation> = emptyList(),
    val aiInsights: List<AiTextInsight> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class HomeGestorViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val projectRepository: IProjectRepository,
    private val orientationRepository: IOrientationRepository,
    private val userRepository: IUserRepository,
    private val aiRepository: IAiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeGestorUiState())
    val uiState: StateFlow<HomeGestorUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
        loadData()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun refresh() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first()
                val pending = ideaRepository.getPendingIdeas().first()
                val allIdeas = ideaRepository.getAllIdeas().first()
                val projects = projectRepository.getAllProjects().first()
                val orientations = orientationRepository.getAllOrientations().first()
                val year = LocalDate.now().year
                val month = LocalDate.now().monthValue
                val prevMonth = if (month > 1) month - 1 else 12
                val prevYear = if (month > 1) year else year - 1
                val ideasThisMonth = AnalyticsMetricsCalculator.ideasCreatedInMonth(allIdeas, year, month)
                val ideasPrevMonth = AnalyticsMetricsCalculator.ideasCreatedInMonth(allIdeas, prevYear, prevMonth)
                val projectsThisMonth = AnalyticsMetricsCalculator.projectsStartedInMonth(projects, year, month)
                val projectsPrevMonth = AnalyticsMetricsCalculator.projectsStartedInMonth(projects, prevYear, prevMonth)
                val totalRoi = AnalyticsMetricsCalculator.totalRoiReais(projects)
                val roiThisMonth = projectsThisMonth.sumOf { it.actualRoi ?: it.estimatedRoi }
                val roiPrevMonth = projectsPrevMonth.sumOf { it.actualRoi ?: it.estimatedRoi }
                val conversion = AnalyticsMetricsCalculator.conversionPercent(allIdeas)
                val completed = projects.count { it.status == ProjectStatus.CONCLUIDO }
                val activeDelta = AnalyticsMetricsCalculator.countDelta(
                    projects.count { it.status == ProjectStatus.EM_ANDAMENTO },
                    projectsPrevMonth.count { it.status == ProjectStatus.EM_ANDAMENTO },
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        metrics = GestorMetrics(
                            pendingIdeas = pending.count { i -> i.status == IdeaStatus.AGUARDANDO_ANALISE },
                            approvedIdeas = allIdeas.count { i -> i.status == IdeaStatus.APROVADA },
                            activeProjects = projects.count { p -> p.status == ProjectStatus.EM_ANDAMENTO },
                            completedProjects = completed,
                            inReviewCount = allIdeas.count { it.status == IdeaStatus.EM_ANALISE },
                            approvalRatePercent = AnalyticsMetricsCalculator.approvalRatePercent(allIdeas)
                                .takeIf { allIdeas.isNotEmpty() },
                            totalRoiLabel = formatCurrencyCompact(totalRoi).takeIf { totalRoi > 0 },
                            roiMonthDeltaPercent = AnalyticsMetricsCalculator.percentDelta(
                                roiThisMonth.toInt(),
                                roiPrevMonth.toInt(),
                            ).takeIf { roiThisMonth > 0 || roiPrevMonth > 0 },
                            conversionPercent = conversion.takeIf { allIdeas.isNotEmpty() },
                            conversionMonthDelta = AnalyticsMetricsCalculator.countDelta(
                                AnalyticsMetricsCalculator.conversionPercent(ideasThisMonth),
                                AnalyticsMetricsCalculator.conversionPercent(ideasPrevMonth),
                            ).takeIf { allIdeas.isNotEmpty() },
                            avgPendingDaysLabel = AnalyticsMetricsCalculator.averagePendingDays(allIdeas)
                                ?.let { AnalyticsMetricsCalculator.formatDaysShort(it) },
                            activeProjectsDelta = activeDelta.takeIf { projects.isNotEmpty() },
                        ),
                        pendingIdeas = pending.take(3),
                        activeProjects = projects.filter { p -> p.status == ProjectStatus.EM_ANDAMENTO }.take(2),
                        orientations = orientations.take(2),
                        aiInsights = aiRepository.getGestorHomeInsights(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
