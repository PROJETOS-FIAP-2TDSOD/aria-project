package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.remote.dto.IdeaDto
import com.fiap.ariachallenge.data.remote.dto.IdeaRequestDto
import com.fiap.ariachallenge.data.remote.dto.KeyMetricInputDto
import com.fiap.ariachallenge.data.remote.dto.OrientationDto
import com.fiap.ariachallenge.data.remote.dto.OrientationRequestDto
import com.fiap.ariachallenge.data.remote.dto.OrientationKeyMetricDto
import com.fiap.ariachallenge.domain.model.OrientationKeyMetric
import com.fiap.ariachallenge.data.remote.dto.MilestoneInputDto
import com.fiap.ariachallenge.data.remote.dto.DashboardEstrategiaRoiDto
import com.fiap.ariachallenge.data.remote.dto.DashboardProjetoRoiDto
import com.fiap.ariachallenge.data.remote.dto.DashboardResumoDto
import com.fiap.ariachallenge.data.remote.dto.IndicadorEstrategicoDto
import com.fiap.ariachallenge.data.remote.dto.NotificationDto
import com.fiap.ariachallenge.data.remote.dto.ProjectDto
import com.fiap.ariachallenge.data.remote.dto.ProjectMilestoneDto
import com.fiap.ariachallenge.data.remote.dto.ProjectRequestDto
import com.fiap.ariachallenge.data.remote.dto.ProjectTeamMemberDto
import com.fiap.ariachallenge.data.remote.dto.TeamMemberInputDto
import com.fiap.ariachallenge.data.remote.dto.UserDto
import com.fiap.ariachallenge.domain.model.DashboardSummary
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.ProjectMilestone
import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.IndicadorEstrategico
import com.fiap.ariachallenge.domain.model.Notification
import com.fiap.ariachallenge.domain.model.NotificationType
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectRoiSummary
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.StrategyRoiSummary
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun User.toDto(): UserDto = UserDto(
    id = id,
    name = name,
    email = email,
    role = role.name,
    department = department,
    avatarInitials = avatarInitials,
    totalIdeas = totalIdeas,
    approvedIdeas = approvedIdeas,
    points = points,
    badges = badges,
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = UserRole.valueOf(role),
    department = department,
    avatarInitials = avatarInitials.orEmpty().ifBlank { name.take(2).uppercase() },
    totalIdeas = totalIdeas,
    approvedIdeas = approvedIdeas,
    points = points,
    badges = badges,
)
fun Idea.toDto(): IdeaDto = IdeaDto(
    id = id,
    title = title,
    author = author.toDto(),
    category = category.name,
    description = description,
    problema = problema,
    beneficios = beneficios,
    recursos = recursos,
    status = status.name,
    score = score,
    gestorFeedback = gestorFeedback,
    estimatedRoi = estimatedRoi,
    createdAt = createdAt.format(dateTimeFormatter),
    updatedAt = updatedAt.format(dateTimeFormatter),
)

fun IdeaDto.toDomain(): Idea = Idea(
    id = id,
    title = title,
    author = author.toDomain(),
    category = IdeaCategory.valueOf(category),
    description = description,
    problema = problema,
    beneficios = beneficios,
    recursos = recursos,
    status = IdeaStatus.valueOf(status),
    score = score,
    gestorFeedback = gestorFeedback,
    estimatedRoi = estimatedRoi,
    aiScore = aiScore,
    aiJustification = aiJustification,
    aiAnalyzedAt = aiAnalyzedAt?.let { LocalDateTime.parse(it, dateTimeFormatter) },
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
)

// Payload real de criação/edição (POST/PUT) — só os 6 campos que o backend aceita
fun Idea.toRequestDto(): IdeaRequestDto = IdeaRequestDto(
    title = title,
    category = category.name,
    description = description,
    problema = problema,
    beneficios = beneficios,
    recursos = recursos,
)

fun ProjectMilestone.toDto(): ProjectMilestoneDto = ProjectMilestoneDto(
    id = id,
    title = title,
    dueDate = dueDate.format(dateFormatter),
    status = status.name,
)

fun ProjectMilestoneDto.toDomain(): ProjectMilestone = ProjectMilestone(
    id = id,
    title = title,
    dueDate = LocalDate.parse(dueDate, dateFormatter),
    status = MilestoneStatus.valueOf(status),
)

fun ProjectTeamMember.toDto(): ProjectTeamMemberDto = ProjectTeamMemberDto(
    user = user.toDto(),
    projectRole = projectRole,
)

fun ProjectTeamMemberDto.toDomain(): ProjectTeamMember = ProjectTeamMember(
    user = user.toDomain(),
    projectRole = projectRole,
)

fun Project.toDto(): ProjectDto = ProjectDto(
    id = id,
    title = title,
    description = description,
    originIdea = originIdea.toDto(),
    manager = manager.toDto(),
    status = status.name,
    progress = progress,
    estimatedRoi = estimatedRoi,
    actualRoi = actualRoi,
    budget = budget,
    sponsorLabel = sponsorLabel,
    strategicOrientationLabel = strategicOrientationLabel,
    teamMembers = teamMembers.map { it.toDto() },
    milestones = milestones.map { it.toDto() },
    startDate = startDate.toLocalDate().format(dateFormatter),
    expectedEndDate = expectedEndDate.toLocalDate().format(dateFormatter),
    updatedAt = updatedAt.format(dateTimeFormatter),
)

// Payload real de criacao/edicao (POST/PUT) — so os campos que ProjectRequestDto.java aceita
fun Project.toRequestDto(): ProjectRequestDto = ProjectRequestDto(
    title = title,
    description = description,
    originIdeaId = originIdea.id,
    sponsorLabel = sponsorLabel,
    strategicOrientationLabel = strategicOrientationLabel,
    budget = budget,
    estimatedRoi = estimatedRoi,
    teamMembers = teamMembers.map { it.toInputDto() },
    milestones = milestones.map { it.toInputDto() },
    startDate = startDate.toLocalDate().format(dateFormatter),
    expectedEndDate = expectedEndDate.toLocalDate().format(dateFormatter),
)

fun ProjectTeamMember.toInputDto(): TeamMemberInputDto = TeamMemberInputDto(
    userId = user.id,
    projectRole = projectRole,
)

fun ProjectMilestone.toInputDto(): MilestoneInputDto = MilestoneInputDto(
    title = title,
    dueDate = dueDate.format(dateFormatter),
    status = status.name,
)

fun ProjectDto.toDomain(): Project = Project(
    id = id,
    title = title,
    description = description,
    originIdea = originIdea.toDomain(),
    manager = manager.toDomain(),
    status = ProjectStatus.valueOf(status),
    progress = progress,
    estimatedRoi = estimatedRoi,
    actualRoi = actualRoi,
    budget = budget,
    sponsorLabel = sponsorLabel,
    strategicOrientationLabel = strategicOrientationLabel,
    teamMembers = teamMembers.map { it.toDomain() },
    milestones = milestones.map { it.toDomain() },
    startDate = LocalDate.parse(startDate, dateFormatter).atStartOfDay(),
    expectedEndDate = LocalDate.parse(expectedEndDate, dateFormatter).atStartOfDay(),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
)

fun OrientationKeyMetric.toDto(): OrientationKeyMetricDto = OrientationKeyMetricDto(
    name = name,
    achieved = achieved,
    target = target,
    progress = progress,
)

fun OrientationKeyMetricDto.toDomain(): OrientationKeyMetric = OrientationKeyMetric(
    name = name,
    achieved = achieved,
    target = target,
    progress = progress,
)

// Payload real de criacao/edicao (POST/PUT) — so os campos que OrientationRequestDto.java aceita
fun Orientation.toRequestDto(): OrientationRequestDto = OrientationRequestDto(
    code = code,
    title = title,
    description = description,
    category = category.name,
    priority = priority.name,
    period = period,
    targetRoles = targetRoles.map { it.name },
    keyMetrics = keyMetrics.map { it.toInputDto() },
    expiresAt = expiresAt?.toLocalDate()?.format(dateFormatter),
)

fun OrientationKeyMetric.toInputDto(): KeyMetricInputDto = KeyMetricInputDto(
    name = name,
    achieved = achieved,
    target = target,
    progress = progress,
)

fun Orientation.toDto(): OrientationDto = OrientationDto(
    id = id,
    code = code,
    title = title,
    description = description,
    author = author.toDto(),
    category = category.name,
    priority = priority.name,
    period = period,
    targetRoles = targetRoles.map { it.name },
    keyMetrics = keyMetrics.map { it.toDto() },
    ideasCount = ideasCount,
    ideasDelta = ideasDelta,
    projectsActive = projectsActive,
    roiCompact = roiCompact,
    roiDeltaPercent = roiDeltaPercent,
    progress = progress,
    createdAt = createdAt.format(dateTimeFormatter),
    expiresAt = expiresAt?.format(dateTimeFormatter),
)

fun OrientationDto.toDomain(): Orientation = Orientation(
    id = id,
    code = code,
    title = title,
    description = description,
    author = author.toDomain(),
    category = IdeaCategory.valueOf(category),
    priority = OrientationPriority.valueOf(priority),
    period = period,
    targetRoles = targetRoles.map { UserRole.valueOf(it) },
    keyMetrics = keyMetrics.map { it.toDomain() },
    ideasCount = ideasCount,
    ideasDelta = ideasDelta,
    projectsActive = projectsActive,
    roiCompact = roiCompact,
    roiDeltaPercent = roiDeltaPercent,
    progress = progress,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    expiresAt = expiresAt?.let { LocalDateTime.parse(it, dateTimeFormatter) },
)

fun NotificationDto.toDomain(): Notification = Notification(
    id = id,
    title = title,
    message = message,
    type = NotificationType.valueOf(type),
    isRead = read,
    relatedIdeaId = relatedIdeaId,
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
)

fun IndicadorEstrategicoDto.toDomain(): IndicadorEstrategico = IndicadorEstrategico(
    orientationId = orientationId,
    orientationTitle = orientationTitle,
    name = name,
    achieved = achieved,
    target = target,
    progress = progress,
)

fun DashboardResumoDto.toDomain(): DashboardSummary = DashboardSummary(
    ideasSubmetidas = ideasSubmetidas,
    ideasAprovadas = ideasAprovadas,
    ideasEmAnalise = ideasEmAnalise,
    ideasEmProjeto = ideasEmProjeto,
    taxaAprovacaoPercent = taxaAprovacaoPercent,
    taxaConversaoPercent = taxaConversaoPercent,
    projetosPlanejamento = projetosPlanejamento,
    projetosEmAndamento = projetosEmAndamento,
    projetosConcluidos = projetosConcluidos,
    projetosSuspensos = projetosSuspensos,
    projetosCancelados = projetosCancelados,
    projetosNoPrazo = projetosNoPrazo,
    projetosAtrasados = projetosAtrasados,
    investimentoTotal = investimentoTotal,
    roiTotal = roiTotal,
    lucroTotal = lucroTotal,
    roiDeltaPercent30d = roiDeltaPercent30d,
    indicadoresEstrategicos = indicadoresEstrategicos.map { it.toDomain() },
)

fun DashboardProjetoRoiDto.toDomain(): ProjectRoiSummary = ProjectRoiSummary(
    projectId = projectId,
    titulo = titulo,
    status = ProjectStatus.valueOf(status),
    progresso = progresso,
    investimento = investimento,
    roi = roi,
    lucro = lucro,
    atrasado = atrasado,
)

fun DashboardEstrategiaRoiDto.toDomain(): StrategyRoiSummary = StrategyRoiSummary(
    orientationId = orientationId,
    titulo = titulo,
    categoria = IdeaCategory.valueOf(categoria),
    ideasCount = ideasCount,
    projetosAtivos = projetosAtivos,
    roiCompact = roiCompact,
    roiDeltaPercent = roiDeltaPercent,
    progresso = progresso,
)