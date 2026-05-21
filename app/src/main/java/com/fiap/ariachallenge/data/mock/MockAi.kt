package com.fiap.ariachallenge.data.mock

import android.content.res.Configuration
import com.fiap.ariachallenge.domain.model.AiAnalyzeBrief
import com.fiap.ariachallenge.domain.model.AiAnalyticsBundle
import com.fiap.ariachallenge.domain.model.AiDashboardMonthInsight
import com.fiap.ariachallenge.domain.model.AiEmergingTopic
import com.fiap.ariachallenge.domain.model.AiInsightKind
import com.fiap.ariachallenge.domain.model.AiInsightTone
import com.fiap.ariachallenge.domain.model.AiOrientationAssist
import com.fiap.ariachallenge.domain.model.AiScoreBreakdownItem
import com.fiap.ariachallenge.domain.model.AiSimilarIdeaSuggestion
import com.fiap.ariachallenge.domain.model.AiTextInsight
import com.fiap.ariachallenge.domain.model.AiTimelineEvent
import com.fiap.ariachallenge.util.formatCurrencyCompact

object MockAi {

    private fun L(en: String, pt: String, es: String) = LocalizedMockText(en, pt, es)

    private val operadorSimilarLabel = L(
        en = "SIMILAR IDEA · APPROVED 2023",
        pt = "IDEIA SIMILAR · APROVADA 2023",
        es = "IDEA SIMILAR · APROBADA 2023",
    )

    private val gestorInsights = listOf(
        Triple(
            L(
                en = "3 high-score ideas waiting for review for more than 5 days.",
                pt = "3 ideias com score alto aguardando análise há mais de 5 dias.",
                es = "3 ideas con puntuación alta en espera de revisión desde hace más de 5 días.",
            ),
            AiInsightTone.Accent,
            AiInsightKind.General,
        ),
        Triple(
            L(
                en = "Technology category grew +35% this quarter.",
                pt = "Categoria Tecnologia cresceu +35% este trimestre.",
                es = "La categoría Tecnología creció +35% este trimestre.",
            ),
            AiInsightTone.Success,
            AiInsightKind.General,
        ),
        Triple(
            L(
                en = "Average review time: 2.3 days. Target < 3 days met.",
                pt = "Tempo médio de análise: 2.3 dias. Meta < 3 dias atingida.",
                es = "Tiempo medio de revisión: 2,3 días. Meta < 3 días cumplida.",
            ),
            AiInsightTone.Info,
            AiInsightKind.General,
        ),
    )

    private val defaultIdeaInsights = listOf(
        Triple(
            L(
                en = "High technical feasibility — existing infrastructure covers 80% of requirements.",
                pt = "Alta viabilidade técnica — infraestrutura existente cobre 80% dos requisitos.",
                es = "Alta viabilidad técnica: la infraestructura existente cubre el 80% de los requisitos.",
            ),
            AiInsightTone.Success,
            AiInsightKind.Technical,
        ),
        Triple(
            L(
                en = "Aligns with Strategic Guidance 2026 #3 — Operational Efficiency.",
                pt = "Alinha com Orientação Estratégica 2026 #3 — Eficiência Operacional.",
                es = "Alineada con la Directriz Estratégica 2026 #3 — Eficiencia Operacional.",
            ),
            AiInsightTone.Primary,
            AiInsightKind.Alignment,
        ),
        Triple(
            L(
                en = "Requires estimated initial investment of R$ 80–120k.",
                pt = "Requer investimento inicial estimado em R$ 80–120k.",
                es = "Requiere inversión inicial estimada de R$ 80–120k.",
            ),
            AiInsightTone.Warning,
            AiInsightKind.Investment,
        ),
        Triple(
            L(
                en = "Estimated payback: 8 months · Projected ROI of 240%.",
                pt = "Payback estimado: 8 meses · ROI projetado de 240%.",
                es = "Payback estimado: 8 meses · ROI proyectado del 240%.",
            ),
            AiInsightTone.Info,
            AiInsightKind.Payback,
        ),
    )

    private val defaultScoreBreakdown = listOf(
        L("Technical feasibility", "Viabilidade técnica", "Viabilidad técnica") to 85,
        L("Strategic alignment", "Alinhamento estratégico", "Alineación estratégica") to 80,
        L("Estimated impact", "Impacto estimado", "Impacto estimado") to 75,
        L("Required resources", "Recursos necessários", "Recursos necesarios") to 70,
        L("Risk", "Risco", "Riesgo") to 75,
    )

    private val defaultAnalyzeBrief = AiAnalyzeBriefData(
        strengths = listOf(
            L("High technical feasibility", "Alta viabilidade técnica", "Alta viabilidad técnica"),
            L("Strong alignment with guidance #3", "Forte alinhamento com orientação #3", "Fuerte alineación con directriz #3"),
            L("Low implementation complexity", "Baixa complexidade de implementação", "Baja complejidad de implementación"),
        ),
        attention = listOf(
            L("Estimated initial investment of R$ 80k", "Investimento inicial estimado em R$ 80k", "Inversión inicial estimada de R$ 80k"),
            L("Requires integration with legacy system", "Necessita integração com sistema legado", "Requiere integración con sistema legado"),
        ),
        recommendation = L(
            en = "AI recommendation: Approve and create project immediately.",
            pt = "Recomendação IA: Aprovar com criação imediata de projeto.",
            es = "Recomendación IA: Aprobar y crear proyecto de inmediato.",
        ),
        matchLabel = L("HIGH MATCH · 92%", "ALTO MATCH · 92%", "ALTO MATCH · 92%"),
        orientationTitle = L("#3 Operational efficiency", "#3 Eficiência Operacional", "#3 Eficiencia operacional"),
        orientationBody = L(
            en = "Reduce fleet operating cost and increase driver productivity by Q4.",
            pt = "Reduzir custo operacional da frota e aumentar produtividade dos motoristas até Q4.",
            es = "Reducir costo operacional de la flota y aumentar productividad de conductores hasta Q4.",
        ),
    )

    private val orientationAssist = AiOrientationAssistData(
        objective = L(
            en = "Establish partnerships with 5 startups by Q2/2027 to accelerate the innovation pipeline.",
            pt = "Estabelecer parcerias com 5 startups até Q2/2027 para acelerar a esteira de inovação.",
            es = "Establecer alianzas con 5 startups hasta Q2/2027 para acelerar el pipeline de innovación.",
        ),
        metrics = L(
            en = "• Partnerships signed · 5\n• Pilots launched · 8\n• ROI generated · R$ 1.2M",
            pt = "• Parcerias firmadas · 5\n• Pilotos lançados · 8\n• ROI gerado · R$ 1,2M",
            es = "• Alianzas firmadas · 5\n• Pilotos lanzados · 8\n• ROI generado · R$ 1,2M",
        ),
    )

    private val dashboardMonthForecasts: Map<Int, Pair<LocalizedMockText, LocalizedMockText>> = mapOf(
        1 to (
            L(
                en = "32 ideas projected for Feb (+15%) · Expected ROI R$ 260k",
                pt = "32 ideias projetadas para fev (+15%) · ROI esperado R$ 260k",
                es = "32 ideas proyectadas para feb (+15%) · ROI esperado R$ 260k",
            ) to L("Process +22% · Technology stable", "Processo +22% · Tecnologia estável", "Proceso +22% · Tecnología estable")
            ),
        2 to (
            L(
                en = "38 ideas projected for Mar (+18%) · Expected ROI R$ 320k",
                pt = "38 ideias projetadas para mar (+18%) · ROI esperado R$ 320k",
                es = "38 ideas proyectadas para mar (+18%) · ROI esperado R$ 320k",
            ) to L("Technology +28% · Sustainability +12%", "Tecnologia +28% · Sustentabilidade +12%", "Tecnología +28% · Sustentabilidade +12%")
            ),
        3 to (
            L(
                en = "42 ideas projected for Apr (+11%) · Expected ROI R$ 380k",
                pt = "42 ideias projetadas para abr (+11%) · ROI esperado R$ 380k",
                es = "42 ideas proyectadas para abr (+11%) · ROI esperado R$ 380k",
            ) to L("Technology +30% · Product +15%", "Tecnologia +30% · Produto +15%", "Tecnología +30% · Producto +15%")
            ),
        4 to (
            L(
                en = "45 ideas projected for May (+7%) · Expected ROI R$ 420k",
                pt = "45 ideias projetadas para mai (+7%) · ROI esperado R$ 420k",
                es = "45 ideas proyectadas para may (+7%) · ROI esperado R$ 420k",
            ) to L("Technology +25% · Sustainability +18%", "Tecnologia +25% · Sustentabilidade +18%", "Tecnología +25% · Sustentabilidade +18%")
            ),
        5 to (
            L(
                en = "52 ideas projected for Jun (+15%) · Expected ROI R$ 520k",
                pt = "52 ideias projetadas para jun (+15%) · ROI esperado R$ 520k",
                es = "52 ideas proyectadas para jun (+15%) · ROI esperado R$ 520k",
            ) to L("Sustainability +45% · AI / Automation +30%", "Sustentabilidade +45% · IA / Automação +30%", "Sustentabilidade +45% · IA / Automatización +30%")
            ),
    )

    private val analyticsBundle = AiAnalyticsBundleData(
        predictionTitle = L(
            en = "52 ideas · Projected ROI R$ 520k",
            pt = "52 ideias · ROI projetado R$ 520k",
            es = "52 ideas · ROI proyectado R$ 520k",
        ),
        predictionSub = L("FORECAST · NEXT QUARTER", "PREVISÃO · PRÓXIMO TRIMESTRE", "PREVISIÓN · PRÓXIMO TRIMESTRE"),
        predictionBullets = listOf(
            L(
                en = "+15% in idea volume if current trend continues.",
                pt = "+15% no volume de ideias se mantida a tendência atual.",
                es = "+15% en volumen de ideas si se mantiene la tendencia actual.",
            ),
            L(
                en = "Sustainability and AI categories should account for 60% of submissions.",
                pt = "Categorias Sustentabilidade e IA devem dominar 60% das submissões.",
                es = "Las categorías Sustentabilidade e IA deben representar el 60% de los envíos.",
            ),
            L(
                en = "Recommendation: create dedicated AI guidance to channel the flow.",
                pt = "Recomendação: criar orientação dedicada à IA para canalizar o fluxo.",
                es = "Recomendación: crear directriz dedicada a IA para canalizar el flujo.",
            ),
        ),
        recommendations = listOf(
            L(
                en = "Publish \"AI applied to operations\" guidance — high concentration of related ideas.",
                pt = "Publicar orientação \"IA aplicada à operação\" — alta concentração de ideias afins.",
                es = "Publicar directriz \"IA aplicada a la operación\" — alta concentración de ideas afines.",
            ),
            L(
                en = "Re-review 3 ideas rejected in 2024 — technical feasibility has changed.",
                pt = "Reanalisar 3 ideias rejeitadas em 2024 — viabilidade técnica mudou.",
                es = "Revisar 3 ideas rechazadas en 2024 — la viabilidad técnica cambió.",
            ),
            L(
                en = "Accelerate \"Checklist app\" project — realized ROI 35% below plan.",
                pt = "Acelerar projeto \"App de checklist\" — ROI realizado abaixo do plano em 35%.",
                es = "Acelerar proyecto \"App de checklist\" — ROI realizado 35% por debajo del plan.",
            ),
        ),
        emerging = listOf(
            L("ESG Sustainability", "Sustentabilidade ESG", "Sostenibilidad ESG") to L(
                en = "14 ideas submitted in 30 days · 4 with score > 75. Recommendation: prioritize review.",
                pt = "14 ideias submetidas em 30 dias · 4 com score > 75. Recomendação: priorizar análise.",
                es = "14 ideas enviadas en 30 días · 4 con puntuación > 75. Recomendación: priorizar revisión.",
            ),
            L("AI applied to operations", "IA aplicada à operação", "IA aplicada a la operación") to L(
                en = "9 ideas · focused on routing and predictive maintenance.",
                pt = "9 ideias · concentradas em roteirização e manutenção preditiva.",
                es = "9 ideas · centradas en ruteo y mantenimiento predictivo.",
            ),
        ),
    )

    fun getOperadorHomeSuggestion(configuration: Configuration, sourceIdeaId: String = "i2"): AiSimilarIdeaSuggestion? {
        val idea = MockIdeas.getById(sourceIdeaId) ?: return null
        val roiReais = idea.estimatedRoi ?: 120_000.0
        return AiSimilarIdeaSuggestion(
            label = operadorSimilarLabel.resolve(configuration),
            title = idea.title,
            body = idea.description,
            score = idea.score ?: 85,
            roiLabel = "ROI ${formatCurrencyCompact(roiReais)}",
            sourceIdeaId = idea.id,
        )
    }

    fun getGestorHomeInsights(configuration: Configuration): List<AiTextInsight> =
        gestorInsights.map { (text, tone, kind) ->
            AiTextInsight(text.resolve(configuration), tone, kind)
        }

    fun getIdeaAnalysis(configuration: Configuration, ideaId: String): List<AiTextInsight> {
        val idea = MockIdeas.getById(ideaId)
        if (idea == null) return resolveInsights(configuration, defaultIdeaInsights)
        val custom = when (idea.id) {
            "i6" -> listOf(
                Triple(
                    L(
                        en = "LGPD compliance must be addressed before approval.",
                        pt = "Conformidade com LGPD deve ser endereçada antes da aprovação.",
                        es = "El cumplimiento LGPD debe abordarse antes de la aprobación.",
                    ),
                    AiInsightTone.Warning,
                    AiInsightKind.General,
                ),
            )
            "i9" -> listOf(
                Triple(
                    L(
                        en = "Exceptional safety impact — aligns with fleet risk reduction goals.",
                        pt = "Impacto excepcional em segurança — alinha com metas de redução de risco da frota.",
                        es = "Impacto excepcional en seguridad — alinea con metas de reducción de riesgo.",
                    ),
                    AiInsightTone.Success,
                    AiInsightKind.Technical,
                ),
            ) + defaultIdeaInsights.drop(1)
            else -> defaultIdeaInsights
        }
        return resolveInsights(configuration, custom)
    }

    fun getScoreBreakdown(configuration: Configuration, ideaId: String): List<AiScoreBreakdownItem> {
        val idea = MockIdeas.getById(ideaId)
        val multiplier = when {
            (idea?.score ?: 0) >= 90 -> 1.0
            (idea?.score ?: 0) >= 75 -> 0.95
            else -> 0.88
        }
        return defaultScoreBreakdown.map { (label, base) ->
            AiScoreBreakdownItem(
                label = label.resolve(configuration),
                value = (base * multiplier).toInt().coerceIn(0, 100),
            )
        }
    }

    fun getAnalyzeBrief(configuration: Configuration, ideaId: String): AiAnalyzeBrief {
        val data = defaultAnalyzeBrief
        return AiAnalyzeBrief(
            strengths = data.strengths.map { it.resolve(configuration) },
            attentionPoints = data.attention.map { it.resolve(configuration) },
            recommendation = data.recommendation.resolve(configuration),
            orientationMatchLabel = data.matchLabel.resolve(configuration),
            orientationTitle = data.orientationTitle.resolve(configuration),
            orientationBody = data.orientationBody.resolve(configuration),
        )
    }

    fun getOrientationAssist(configuration: Configuration): AiOrientationAssist =
        AiOrientationAssist(
            objective = orientationAssist.objective.resolve(configuration),
            keyMetrics = orientationAssist.metrics.resolve(configuration),
        )

    fun getDashboardMonthInsight(configuration: Configuration, month: Int): AiDashboardMonthInsight {
        val (forecast, emerging) = dashboardMonthForecasts[month] ?: dashboardMonthForecasts.getValue(5)
        return AiDashboardMonthInsight(
            forecastBody = forecast.resolve(configuration),
            emergingBody = emerging.resolve(configuration),
        )
    }

    fun getAnalyticsBundle(configuration: Configuration): AiAnalyticsBundle {
        val data = analyticsBundle
        return AiAnalyticsBundle(
            predictionTitle = data.predictionTitle.resolve(configuration),
            predictionSub = data.predictionSub.resolve(configuration),
            predictionBullets = data.predictionBullets.map { it.resolve(configuration) },
            recommendations = data.recommendations.map { it.resolve(configuration) },
            emergingTopics = data.emerging.mapIndexed { index, (title, body) ->
                AiEmergingTopic(
                    title = title.resolve(configuration),
                    body = body.resolve(configuration),
                    tone = if (index == 0) AiInsightTone.Accent else AiInsightTone.Info,
                )
            },
        )
    }

    fun getIdeaTimeline(configuration: Configuration, ideaId: String): List<AiTimelineEvent> {
        val idea = MockIdeas.getById(ideaId)
        return listOf(
            AiTimelineEvent(
                title = L(
                    en = "Calculating score (AI)",
                    pt = "Calculando score (IA)",
                    es = "Calculando puntuación (IA)",
                ).resolve(configuration),
                subtitle = L("2 hours ago", "Há 2 horas", "Hace 2 horas").resolve(configuration),
                isLive = true,
            ),
            AiTimelineEvent(
                title = L(
                    en = "Forwarded to manager",
                    pt = "Encaminhada para gestora",
                    es = "Enviada al gestor",
                ).resolve(configuration),
                subtitle = L("2 hours ago", "Há 2 horas", "Hace 2 horas").resolve(configuration),
            ),
            AiTimelineEvent(
                title = L("Idea submitted", "Ideia submetida", "Idea enviada").resolve(configuration),
                subtitle = idea?.createdAt?.toString().orEmpty(),
            ),
        )
    }

    private fun resolveInsights(
        configuration: Configuration,
        source: List<Triple<LocalizedMockText, AiInsightTone, AiInsightKind>>,
    ): List<AiTextInsight> = source.map { (text, tone, kind) ->
        AiTextInsight(text.resolve(configuration), tone, kind)
    }

    private data class AiAnalyzeBriefData(
        val strengths: List<LocalizedMockText>,
        val attention: List<LocalizedMockText>,
        val recommendation: LocalizedMockText,
        val matchLabel: LocalizedMockText,
        val orientationTitle: LocalizedMockText,
        val orientationBody: LocalizedMockText,
    )

    private data class AiOrientationAssistData(
        val objective: LocalizedMockText,
        val metrics: LocalizedMockText,
    )

    private data class AiAnalyticsBundleData(
        val predictionTitle: LocalizedMockText,
        val predictionSub: LocalizedMockText,
        val predictionBullets: List<LocalizedMockText>,
        val recommendations: List<LocalizedMockText>,
        val emerging: List<Pair<LocalizedMockText, LocalizedMockText>>,
    )
}
