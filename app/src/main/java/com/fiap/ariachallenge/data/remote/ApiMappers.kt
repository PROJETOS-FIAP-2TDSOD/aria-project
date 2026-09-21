package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.remote.dto.IdeaDto
import com.fiap.ariachallenge.data.remote.dto.OrientationDto
import com.fiap.ariachallenge.data.remote.dto.OrientationKeyMetricDto
import com.fiap.ariachallenge.domain.model.OrientationKeyMetric
import com.fiap.ariachallenge.data.remote.dto.ProjectDto
import com.fiap.ariachallenge.data.remote.dto.ProjectMilestoneDto
import com.fiap.ariachallenge.data.remote.dto.ProjectTeamMemberDto
import com.fiap.ariachallenge.data.remote.dto.UserDto
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.ProjectMilestone
import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
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
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = UserRole.valueOf(role),
    department = department,
    avatarInitials = avatarInitials.ifBlank { name.take(2).uppercase() },
    totalIdeas = totalIdeas,
    approvedIdeas = approvedIdeas,
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
    createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
    updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
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
    startDate = startDate.format(dateTimeFormatter),
    expectedEndDate = expectedEndDate.format(dateTimeFormatter),
    updatedAt = updatedAt.format(dateTimeFormatter),
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
    startDate = LocalDateTime.parse(startDate, dateTimeFormatter),
    expectedEndDate = LocalDateTime.parse(expectedEndDate, dateTimeFormatter),
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
