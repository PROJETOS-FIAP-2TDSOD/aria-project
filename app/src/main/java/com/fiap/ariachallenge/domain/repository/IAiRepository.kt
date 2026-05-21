package com.fiap.ariachallenge.domain.repository

import com.fiap.ariachallenge.domain.model.AiAnalyzeBrief
import com.fiap.ariachallenge.domain.model.AiAnalyticsBundle
import com.fiap.ariachallenge.domain.model.AiDashboardMonthInsight
import com.fiap.ariachallenge.domain.model.AiOrientationAssist
import com.fiap.ariachallenge.domain.model.AiScoreBreakdownItem
import com.fiap.ariachallenge.domain.model.AiSimilarIdeaSuggestion
import com.fiap.ariachallenge.domain.model.AiTextInsight
import com.fiap.ariachallenge.domain.model.AiTimelineEvent

interface IAiRepository {
    suspend fun getOperadorHomeSuggestion(userId: String): AiSimilarIdeaSuggestion?
    suspend fun getGestorHomeInsights(): List<AiTextInsight>
    suspend fun getIdeaAnalysis(ideaId: String): List<AiTextInsight>
    suspend fun getScoreBreakdown(ideaId: String): List<AiScoreBreakdownItem>
    suspend fun getIdeaTimeline(ideaId: String): List<AiTimelineEvent>
    suspend fun getAnalyzeBrief(ideaId: String): AiAnalyzeBrief
    suspend fun getOrientationAssist(): AiOrientationAssist
    suspend fun getDashboardMonthInsight(month: Int): AiDashboardMonthInsight
    suspend fun getAnalyticsBundle(): AiAnalyticsBundle
}
