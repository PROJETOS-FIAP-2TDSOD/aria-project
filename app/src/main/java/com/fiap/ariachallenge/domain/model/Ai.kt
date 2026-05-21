package com.fiap.ariachallenge.domain.model

enum class AiInsightTone {
    Accent,
    Success,
    Info,
    Warning,
    Primary,
}

enum class AiInsightKind {
    Technical,
    Alignment,
    Investment,
    Payback,
    General,
}

data class AiTextInsight(
    val message: String,
    val tone: AiInsightTone,
    val kind: AiInsightKind = AiInsightKind.General,
)

data class AiScoreBreakdownItem(
    val label: String,
    val value: Int,
)

data class AiSimilarIdeaSuggestion(
    val label: String,
    val title: String,
    val body: String,
    val score: Int,
    val roiLabel: String,
    val sourceIdeaId: String,
)

data class AiAnalyzeBrief(
    val strengths: List<String>,
    val attentionPoints: List<String>,
    val recommendation: String,
    val orientationMatchLabel: String,
    val orientationTitle: String,
    val orientationBody: String,
)

data class AiOrientationAssist(
    val objective: String,
    val keyMetrics: String,
)

data class AiDashboardMonthInsight(
    val forecastBody: String,
    val emergingBody: String,
)

data class AiEmergingTopic(
    val title: String,
    val body: String,
    val tone: AiInsightTone,
)

data class AiAnalyticsBundle(
    val predictionTitle: String,
    val predictionSub: String,
    val predictionBullets: List<String>,
    val recommendations: List<String>,
    val emergingTopics: List<AiEmergingTopic>,
)

data class AiTimelineEvent(
    val title: String,
    val subtitle: String,
    val isLive: Boolean = false,
)
