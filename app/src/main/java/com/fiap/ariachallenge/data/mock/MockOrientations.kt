package com.fiap.ariachallenge.data.mock

import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationKeyMetric
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.model.UserRole
import java.time.LocalDateTime

object MockOrientations {

    val orientation1 = orientation(
        id = "o1",
        code = "#01",
        title = "Foco Q3: Redução de Custos Operacionais",
        description = "Para o terceiro trimestre, priorizamos ideias que impactem diretamente nos custos operacionais da frota. Foco em combustível, manutenção e tempo de rota. Ideias com ROI estimado acima de R\$100k serão priorizadas para análise imediata.",
        category = IdeaCategory.FINANCEIRO,
        priority = OrientationPriority.ALTA,
        period = "Q3 2026 — Q2 2027",
        keyMetrics = listOf(
            metric("Ideias alinhadas", "12", "15", 0.8f),
            metric("Projetos ativos", "3", "4", 0.75f),
            metric("ROI realizado", "420k", "500k", 0.84f),
        ),
        ideasCount = 12,
        ideasDelta = 4,
        projectsActive = 3,
        roiCompact = "420k",
        roiDeltaPercent = 18,
        progress = 0.8f,
        createdDaysAgo = 15,
        expiresDaysAhead = 75,
    )

    val orientation2 = orientation(
        id = "o2",
        code = "#02",
        title = "Digitalização: Meta de Papel Zero até Dezembro",
        description = "A empresa tem a meta de eliminar 80% dos documentos físicos até dezembro. Precisamos de ideias que acelerem a digitalização de processos, especialmente na operação de campo e manutenção.",
        category = IdeaCategory.PROCESSO,
        priority = OrientationPriority.CRITICA,
        period = "Q4 2026 — Q1 2027",
        keyMetrics = listOf(
            metric("Processos digitalizados", "18", "25", 0.72f),
            metric("Redução de papel", "62%", "80%", 0.78f),
            metric("Adoção mobile", "340", "400", 0.85f),
        ),
        ideasCount = 9,
        ideasDelta = 3,
        projectsActive = 2,
        roiCompact = "180k",
        roiDeltaPercent = 12,
        progress = 0.72f,
        createdDaysAgo = 8,
        expiresDaysAhead = 45,
    )

    val orientation3 = orientation(
        id = "o3",
        code = "#03",
        title = "ESG 2026: Sustentabilidade na Operação",
        description = "Nosso relatório ESG 2026 precisa demonstrar avanços concretos. Buscamos ideias em energia limpa, redução de emissões e gestão de resíduos.",
        category = IdeaCategory.SUSTENTABILIDADE,
        priority = OrientationPriority.MEDIA,
        period = "Q2 2026 — Q4 2026",
        keyMetrics = listOf(
            metric("Emissões CO₂", "8.2t", "6t", 0.55f),
            metric("Energia renovável", "35%", "50%", 0.7f),
            metric("Resíduos reciclados", "71%", "85%", 0.84f),
        ),
        ideasCount = 7,
        ideasDelta = 2,
        projectsActive = 1,
        roiCompact = "95k",
        roiDeltaPercent = 9,
        progress = 0.65f,
        createdDaysAgo = 30,
        expiresMonthsAhead = 6,
    )

    val orientation4 = orientation(
        id = "o4",
        code = "#04",
        title = "Segurança em primeiro lugar — Campanha Zero Acidentes",
        description = "Com o aumento de 12% nos acidentes no primeiro semestre, temos campanha ativa de Zero Acidentes.",
        category = IdeaCategory.SEGURANCA,
        priority = OrientationPriority.CRITICA,
        period = "Q3 2026 — Q1 2027",
        keyMetrics = listOf(
            metric("Acidentes evitados", "14", "20", 0.7f),
            metric("Treinamentos concluídos", "890", "1000", 0.89f),
            metric("Conformidade EPI", "94%", "98%", 0.96f),
        ),
        ideasCount = 11,
        ideasDelta = 5,
        projectsActive = 2,
        roiCompact = "210k",
        roiDeltaPercent = 15,
        progress = 0.78f,
        createdDaysAgo = 5,
        expiresMonthsAhead = 3,
    )

    val orientation5 = orientation(
        id = "o5",
        code = "#05",
        title = "Experiência do Motorista — Retenção de Talentos",
        description = "A rotatividade de motoristas está em 28% ao ano. Precisamos de ideias que melhorem a experiência do motorista.",
        category = IdeaCategory.PESSOAS,
        priority = OrientationPriority.ALTA,
        period = "Q3 2026 — Q4 2026",
        keyMetrics = listOf(
            metric("Turnover", "22%", "18%", 0.6f),
            metric("NPS motoristas", "68", "75", 0.91f),
            metric("Programas ativos", "4", "6", 0.67f),
        ),
        ideasCount = 6,
        ideasDelta = 1,
        projectsActive = 1,
        roiCompact = "75k",
        roiDeltaPercent = 8,
        progress = 0.58f,
        createdDaysAgo = 12,
        expiresDaysAhead = 60,
    )

    val allOrientations = listOf(orientation1, orientation2, orientation3, orientation4, orientation5)

    fun getById(id: String) = allOrientations.find { it.id == id }

    fun getByPriority(priority: OrientationPriority) =
        allOrientations.filter { it.priority == priority }

    private fun metric(name: String, achieved: String, target: String, progress: Float) =
        OrientationKeyMetric(name = name, achieved = achieved, target = target, progress = progress)

    private fun orientation(
        id: String,
        code: String,
        title: String,
        description: String,
        category: IdeaCategory,
        priority: OrientationPriority,
        period: String,
        keyMetrics: List<OrientationKeyMetric>,
        ideasCount: Int,
        ideasDelta: Int,
        projectsActive: Int,
        roiCompact: String,
        roiDeltaPercent: Int,
        progress: Float,
        createdDaysAgo: Long,
        expiresDaysAhead: Long? = null,
        expiresMonthsAhead: Long? = null,
    ): Orientation {
        val expiresAt = when {
            expiresDaysAhead != null -> LocalDateTime.now().plusDays(expiresDaysAhead)
            expiresMonthsAhead != null -> LocalDateTime.now().plusMonths(expiresMonthsAhead)
            else -> null
        }
        return Orientation(
            id = id,
            code = code,
            title = title,
            description = description,
            author = MockUsers.lider1,
            category = category,
            priority = priority,
            period = period,
            targetRoles = listOf(UserRole.OPERADOR, UserRole.GESTOR),
            keyMetrics = keyMetrics,
            ideasCount = ideasCount,
            ideasDelta = ideasDelta,
            projectsActive = projectsActive,
            roiCompact = roiCompact,
            roiDeltaPercent = roiDeltaPercent,
            progress = progress,
            createdAt = LocalDateTime.now().minusDays(createdDaysAgo),
            expiresAt = expiresAt,
        )
    }
}
