package com.fiap.ariachallenge.data.mock

import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import java.time.LocalDateTime

object MockProjects {

    private val roleSponsor = "Sponsor"
    private val roleIdealizer = "Idealizador"
    private val roleTechLead = "Tech Lead"
    private val rolePm = "PM"
    private const val defaultSponsor = "Ana Silva — Logística"
    private const val defaultOrientation = "#1 Transformação digital"

    val project1 = Project(
        id = "p1",
        title = "Rota Inteligente 2.0",
        description = "Implementação de sistema de roteirização com dados de tráfego em tempo real para toda a frota nacional.",
        originIdea = MockIdeas.idea3,
        manager = MockUsers.gestor1,
        status = ProjectStatus.EM_ANDAMENTO,
        progress = 65,
        estimatedRoi = 1200000.0,
        budget = 380000.0,
        sponsorLabel = defaultSponsor,
        strategicOrientationLabel = defaultOrientation,
        teamMembers = MockProjectMilestones.teamFor(
            ProjectTeamMember(MockUsers.gestor1, roleSponsor),
            ProjectTeamMember(MockUsers.operador1, roleIdealizer),
            ProjectTeamMember(MockUsers.operador2, roleTechLead),
        ),
        milestones = MockProjectMilestones.defaultRoadmap(),
        startDate = LocalDateTime.now().minusMonths(2),
        expectedEndDate = LocalDateTime.now().plusMonths(1),
        updatedAt = LocalDateTime.now().minusDays(1),
    )

    val project2 = Project(
        id = "p2",
        title = "DigitaMaint — Checklist Digital",
        description = "Digitalização completa dos processos de manutenção com app para mecânicos e dashboard gerencial.",
        originIdea = MockIdeas.idea2,
        manager = MockUsers.gestor2,
        status = ProjectStatus.PLANEJAMENTO,
        progress = 15,
        estimatedRoi = 420000.0,
        budget = 120000.0,
        sponsorLabel = "João Costa — Operações",
        strategicOrientationLabel = "#3 Eficiência operacional",
        teamMembers = MockProjectMilestones.teamFor(
            ProjectTeamMember(MockUsers.gestor2, roleSponsor),
            ProjectTeamMember(MockUsers.operador2, roleIdealizer),
        ),
        milestones = MockProjectMilestones.defaultRoadmap(LocalDateTime.now().plusMonths(2).toLocalDate()),
        startDate = LocalDateTime.now().minusWeeks(2),
        expectedEndDate = LocalDateTime.now().plusMonths(4),
        updatedAt = LocalDateTime.now().minusDays(3),
    )

    val project3 = Project(
        id = "p3",
        title = "GuardIA — Câmeras Antifadiga",
        description = "Deploy de câmeras com IA para monitoramento de fadiga em toda a frota de longa distância.",
        originIdea = MockIdeas.idea9,
        manager = MockUsers.gestor1,
        status = ProjectStatus.EM_ANDAMENTO,
        progress = 40,
        estimatedRoi = 5000000.0,
        actualRoi = 1800000.0,
        budget = 750000.0,
        sponsorLabel = defaultSponsor,
        strategicOrientationLabel = "#2 Experiência do cliente",
        teamMembers = MockProjectMilestones.teamFor(
            ProjectTeamMember(MockUsers.gestor1, roleSponsor),
            ProjectTeamMember(MockUsers.operador1, roleIdealizer),
            ProjectTeamMember(MockUsers.operador3, roleTechLead),
            ProjectTeamMember(MockUsers.operador4, rolePm),
        ),
        milestones = MockProjectMilestones.defaultRoadmap(LocalDateTime.now().minusMonths(3).toLocalDate()),
        startDate = LocalDateTime.now().minusMonths(3),
        expectedEndDate = LocalDateTime.now().plusMonths(3),
        updatedAt = LocalDateTime.now().minusHours(4),
    )

    val project4 = Project(
        id = "p4",
        title = "Solar Terminals",
        description = "Instalação de energia solar nos 12 terminais logísticos da rede.",
        originIdea = MockIdeas.idea5,
        manager = MockUsers.gestor2,
        status = ProjectStatus.PLANEJAMENTO,
        progress = 8,
        estimatedRoi = 2100000.0,
        budget = 900000.0,
        sponsorLabel = "Maria Santos — Comercial",
        strategicOrientationLabel = defaultOrientation,
        teamMembers = MockProjectMilestones.teamFor(
            ProjectTeamMember(MockUsers.operador3, roleIdealizer),
        ),
        milestones = emptyList(),
        startDate = LocalDateTime.now().minusWeeks(1),
        expectedEndDate = LocalDateTime.now().plusMonths(8),
        updatedAt = LocalDateTime.now().minusDays(2),
    )

    val project5 = Project(
        id = "p5",
        title = "Piloto Gamificação Frota Sul",
        description = "Projeto piloto do sistema de gamificação para motoristas na regional Sul, com 200 participantes.",
        originIdea = MockIdeas.idea1,
        manager = MockUsers.gestor1,
        status = ProjectStatus.CONCLUIDO,
        progress = 100,
        estimatedRoi = 850000.0,
        actualRoi = 920000.0,
        budget = 95000.0,
        sponsorLabel = defaultSponsor,
        strategicOrientationLabel = defaultOrientation,
        teamMembers = MockProjectMilestones.teamFor(
            ProjectTeamMember(MockUsers.operador1, roleIdealizer),
            ProjectTeamMember(MockUsers.operador2, roleTechLead),
            ProjectTeamMember(MockUsers.operador4, rolePm),
        ),
        milestones = MockProjectMilestones.defaultRoadmap(LocalDateTime.now().minusMonths(6).toLocalDate()),
        startDate = LocalDateTime.now().minusMonths(6),
        expectedEndDate = LocalDateTime.now().minusMonths(1),
        updatedAt = LocalDateTime.now().minusMonths(1),
    )

    val allProjects = listOf(project1, project2, project3, project4, project5)

    fun getByStatus(status: ProjectStatus) = allProjects.filter { it.status == status }
    fun getById(id: String) = allProjects.find { it.id == id }

    val totalRoi = allProjects.sumOf { it.actualRoi ?: it.estimatedRoi }
    val activeProjects = allProjects.count { it.status == ProjectStatus.EM_ANDAMENTO }
    val completedProjects = allProjects.count { it.status == ProjectStatus.CONCLUIDO }
}
