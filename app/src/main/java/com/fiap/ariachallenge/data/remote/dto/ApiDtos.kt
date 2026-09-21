package com.fiap.ariachallenge.data.remote.dto

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String = "",
    val avatarInitials: String? = null,
    val totalIdeas: Int = 0,
    val approvedIdeas: Int = 0,
    val points: Int = 0,
    val badges: List<String> = emptyList(),
)

data class IdeaDto(
    val id: String,
    val title: String,
    val author: UserDto,
    val category: String,
    val description: String,
    val problema: String,
    val beneficios: String,
    val recursos: String,
    val status: String,
    val score: Int? = null,
    val gestorFeedback: String? = null,
    val estimatedRoi: Double? = null,
    val aiScore: Int? = null,
    val aiJustification: String? = null,
    val aiAnalyzedAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

// Payload de POST/PUT /ideas — espelha IdeaRequestDto.java (sem id/author/status/score)
data class IdeaRequestDto(
    val title: String,
    val category: String,
    val description: String,
    val problema: String,
    val beneficios: String,
    val recursos: String,
)

// Payload de PATCH /ideas/{id}/review — espelha IdeaReviewDto.java
data class IdeaReviewDto(
    val status: String,
    val score: Int? = null,
    val gestorFeedback: String? = null,
)

data class ProjectMilestoneDto(
    val id: String,
    val title: String,
    val dueDate: String,
    val status: String,
)

data class ProjectTeamMemberDto(
    val user: UserDto,
    val projectRole: String,
)

data class ProjectDto(
    val id: String,
    val title: String,
    val description: String,
    val originIdea: IdeaDto,
    val manager: UserDto,
    val status: String,
    val progress: Int = 0,
    val estimatedRoi: Double = 0.0,
    val actualRoi: Double? = null,
    val budget: Double = 0.0,
    val sponsorLabel: String = "",
    val strategicOrientationLabel: String = "",
    val teamMembers: List<ProjectTeamMemberDto> = emptyList(),
    val milestones: List<ProjectMilestoneDto> = emptyList(),
    val startDate: String,
    val expectedEndDate: String,
    val updatedAt: String,
)

// Payload de POST/PUT /projects — espelha ProjectRequestDto.java (originIdeaId em vez de objeto aninhado)
data class ProjectRequestDto(
    val title: String,
    val description: String,
    val originIdeaId: String,
    val sponsorLabel: String,
    val strategicOrientationLabel: String,
    val budget: Double = 0.0,
    val estimatedRoi: Double = 0.0,
    val teamMembers: List<TeamMemberInputDto> = emptyList(),
    val milestones: List<MilestoneInputDto> = emptyList(),
    val startDate: String,
    val expectedEndDate: String,
)

data class TeamMemberInputDto(
    val userId: String,
    val projectRole: String,
)

data class MilestoneInputDto(
    val title: String,
    val dueDate: String,
    val status: String? = null,
)

// Payload de PATCH /projects/{id}/progress — espelha ProjectUpdateProgressDto.java
data class ProjectUpdateProgressDto(
    val status: String? = null,
    val progress: Int? = null,
    val actualRoi: Double? = null,
)

data class OrientationKeyMetricDto(
    val name: String,
    val achieved: String,
    val target: String,
    val progress: Float = 0f,
)

data class OrientationDto(
    val id: String,
    val code: String = "",
    val title: String,
    val description: String,
    val author: UserDto,
    val category: String,
    val priority: String,
    val period: String = "",
    val targetRoles: List<String>,
    val keyMetrics: List<OrientationKeyMetricDto> = emptyList(),
    val ideasCount: Int = 0,
    val ideasDelta: Int = 0,
    val projectsActive: Int = 0,
    val roiCompact: String = "0k",
    val roiDeltaPercent: Int = 0,
    val progress: Float = 0f,
    val createdAt: String,
    val expiresAt: String? = null,
)

// Payload de POST/PUT /orientations — espelha OrientationRequestDto.java (sem os
// campos calculados: author, ideasCount, roiCompact, progress, etc.)
data class OrientationRequestDto(
    val code: String = "",
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val period: String = "",
    val targetRoles: List<String>,
    val keyMetrics: List<KeyMetricInputDto> = emptyList(),
    val expiresAt: String? = null, // yyyy-MM-dd
)

data class KeyMetricInputDto(
    val name: String,
    val achieved: String,
    val target: String,
    val progress: Float = 0f,
)

data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val read: Boolean = false,
    val relatedIdeaId: String? = null,
    val createdAt: String,
)

data class IndicadorEstrategicoDto(
    val orientationId: String,
    val orientationTitle: String,
    val name: String,
    val achieved: String,
    val target: String,
    val progress: Float = 0f,
)

data class DashboardResumoDto(
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
    val investimentoTotalCompact: String = "0",
    val roiTotalCompact: String = "0",
    val lucroTotalCompact: String = "0",
    val roiDeltaPercent30d: Int = 0,
    val indicadoresEstrategicos: List<IndicadorEstrategicoDto> = emptyList(),
)

data class DashboardEstrategiaRoiDto(
    val orientationId: String,
    val titulo: String,
    val categoria: String,
    val ideasCount: Int = 0,
    val projetosAtivos: Int = 0,
    val roiCompact: String = "0",
    val roiDeltaPercent: Int = 0,
    val progresso: Float = 0f,
)

data class DashboardProjetoRoiDto(
    val projectId: String,
    val titulo: String,
    val status: String,
    val progresso: Int = 0,
    val investimento: Double = 0.0,
    val roi: Double = 0.0,
    val lucro: Double = 0.0,
    val roiCompact: String = "0",
    val dataInicio: String,
    val prazoFinal: String,
    val atrasado: Boolean = false,
)