package com.fiap.ariachallenge.ui.lider.analises

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.analytics.AnalyticsMetricsCalculator
import com.fiap.ariachallenge.domain.model.AiEmergingTopic
import com.fiap.ariachallenge.domain.model.AiInsightTone
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.util.formatCurrencyCompact
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MonthRoiUi(
    @StringRes val monthRes: Int,
    val value: Float,
)

data class ProjectRoiUi(
    val title: String,
    val realizedReais: Double,
    val targetReais: Double,
)

data class TrendUi(
    @StringRes val labelRes: Int,
    val percent: Int,
    val changeLabel: String,
    val up: Boolean,
)

data class EmergingUi(
    val title: String,
    val body: String,
    val tone: EmergingTone,
)

enum class EmergingTone { Accent, Info }

data class AiPredictionUi(
    val title: String,
    val accentColor: AiAccentColor,
)

enum class AiAccentColor { Success, Accent, Info }

data class AiRecommendationUi(
    val text: String,
)

data class AnaliseUiState(
    val isLoading: Boolean = true,
    val selectedTab: Int = 0,
    val selectedRoiMonthIndex: Int = 0,
    val roiTotalFormatted: String = "",
    val roiDeltaPercent: Int = 0,
    val months: List<MonthRoiUi> = emptyList(),
    val topProjects: List<ProjectRoiUi> = emptyList(),
    val trends: List<TrendUi> = emptyList(),
    val emerging: List<EmergingUi> = emptyList(),
    val aiPredictionTitle: String = "",
    val aiPredictionSub: String = "",
    val aiPredictions: List<AiPredictionUi> = emptyList(),
    val aiRecommendations: List<AiRecommendationUi> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class AnaliseViewModel @Inject constructor(
    private val aiRepository: IAiRepository,
    private val ideaRepository: IIdeaRepository,
    private val projectRepository: IProjectRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnaliseUiState())
    val uiState: StateFlow<AnaliseUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    fun onTabSelected(index: Int) = _uiState.update { it.copy(selectedTab = index) }

    fun onRoiMonthSelected(index: Int) =
        _uiState.update { st ->
            val maxIdx = st.months.lastIndex.coerceAtLeast(0)
            st.copy(selectedRoiMonthIndex = index.coerceIn(0, maxIdx))
        }

    private var exportSnapshot: String = ""

    fun getExportText(): String = exportSnapshot

    fun updateExportSnapshot(text: String) {
        exportSnapshot = text
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val ideas = ideaRepository.getAllIdeas().first()
                val projects = projectRepository.getAllProjects().first()
                val year = LocalDate.now().year
                val monthCount = LocalDate.now().monthValue.coerceAtLeast(1)
                val monthLabels = DashboardMonthLabels.MONTH_LABEL_RES.take(monthCount)
                val monthlyRoi = AnalyticsMetricsCalculator.monthlyRoiSeries(projects, monthCount, year)
                val months = monthlyRoi.mapIndexed { index, value ->
                    MonthRoiUi(monthLabels[index], value)
                }
                val totalRoiReais = monthlyRoi.sum().toDouble()
                val lastIdx = months.lastIndex.coerceAtLeast(0)
                val prevValue = months.getOrNull(lastIdx - 1)?.value ?: 0f
                val lastValue = months.getOrNull(lastIdx)?.value ?: 0f
                val roiDelta = when {
                    prevValue <= 0f -> if (lastValue <= 0f) 0 else 100
                    else -> (((lastValue - prevValue) / prevValue) * 100f).roundToInt()
                }
                val topProjects = projects
                    .sortedByDescending { it.actualRoi ?: it.estimatedRoi }
                    .take(5)
                    .map { project ->
                        ProjectRoiUi(
                            title = project.title,
                            realizedReais = project.actualRoi ?: 0.0,
                            targetReais = project.estimatedRoi.coerceAtLeast(1.0),
                        )
                    }
                val trends = AnalyticsMetricsCalculator.categorySharePercents(ideas).take(5).map { (category, pct) ->
                    TrendUi(
                        labelRes = category.getDisplayNameRes(),
                        percent = pct,
                        changeLabel = "$pct%",
                        up = true,
                    )
                }
                val aiBundle = aiRepository.getAnalyticsBundle()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        roiTotalFormatted = formatCurrencyCompact(totalRoiReais),
                        roiDeltaPercent = roiDelta,
                        selectedRoiMonthIndex = lastIdx,
                        months = months,
                        topProjects = topProjects,
                        trends = trends,
                        emerging = aiBundle.emergingTopics.map { topic -> topic.toEmergingUi() },
                        aiPredictionTitle = aiBundle.predictionTitle,
                        aiPredictionSub = aiBundle.predictionSub,
                        aiPredictions = aiBundle.predictionBullets.mapIndexed { index, text ->
                            AiPredictionUi(
                                title = text,
                                accentColor = when (index) {
                                    0 -> AiAccentColor.Success
                                    1 -> AiAccentColor.Accent
                                    else -> AiAccentColor.Info
                                },
                            )
                        },
                        aiRecommendations = aiBundle.recommendations.map { AiRecommendationUi(it) },
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun AiEmergingTopic.toEmergingUi(): EmergingUi = EmergingUi(
        title = title,
        body = body,
        tone = when (tone) {
            AiInsightTone.Accent -> EmergingTone.Accent
            else -> EmergingTone.Info
        },
    )
}

private object DashboardMonthLabels {
    val MONTH_LABEL_RES = listOf(
        R.string.month_jan, R.string.month_feb, R.string.month_mar, R.string.month_apr,
        R.string.month_may, R.string.month_jun, R.string.month_jul, R.string.month_aug,
        R.string.month_sep, R.string.month_oct, R.string.month_nov, R.string.month_dec,
    )
}
