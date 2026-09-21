package com.fiap.ariachallenge.domain.model

data class IndicadorEstrategico(
    val orientationId: String,
    val orientationTitle: String,
    val name: String,
    val achieved: String,
    val target: String,
    val progress: Float = 0f,
)

data class DashboardSummary(
    val ideasSubmetidas: Int = 0,
    val ideasAprovadas: Int = 0,
    val ideasEmAnalise: Int = 0,
    val ideasEmProjeto: Int = 0,
    val taxaAprovacaoPercent: Int = 0,
    val taxaConversaoPercent: Int = 0,
    val projetosPlanejamento: Int = 0,
    val projetosEmAndamento: Int = 0,
    val projetosConcluidos: Int = 0,
    val projetosSuspensos: Int = 0,
    val projetosCancelados: Int = 0,
    val projetosNoPrazo: Int = 0,
    val projetosAtrasados: Int = 0,
    val investimentoTotal: Double = 0.0,
    val roiTotal: Double = 0.0,
    val lucroTotal: Double = 0.0,
    val roiDeltaPercent30d: Int = 0,
    val indicadoresEstrategicos: List<IndicadorEstrategico> = emptyList(),
)

data class ProjectRoiSummary(
    val projectId: String,
    val titulo: String,
    val status: ProjectStatus,
    val progresso: Int,
    val investimento: Double,
    val roi: Double,
    val lucro: Double,
    val atrasado: Boolean,
)

data class StrategyRoiSummary(
    val orientationId: String,
    val titulo: String,
    val categoria: IdeaCategory,
    val ideasCount: Int,
    val projetosAtivos: Int,
    val roiDeltaPercent: Int,
    val progresso: Float,
)