package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.dto.ProjectUpdateProgressDto
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toRequestDto
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val api: AriaApiService,
) : IProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> = flow {
        emit(api.getProjects().map { it.toDomain() })
    }.catch { emit(emptyList()) }

    override fun getProjectById(id: String): Flow<Project?> = flow {
        emit(api.getProjects().map { it.toDomain() }.find { it.id == id })
    }.catch { emit(null) }

    override fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = flow {
        emit(api.getProjects().map { it.toDomain() }.filter { it.status == status })
    }.catch { emit(emptyList()) }

    override suspend fun createProject(project: Project): Result<Project> = runCatching {
        api.createProject(project.toRequestDto()).toDomain()
    }

    override suspend fun updateProject(project: Project): Result<Project> = runCatching {
        api.updateProject(project.id, project.toRequestDto()).toDomain()
    }

    override suspend fun updateProjectProgress(
        id: String,
        status: ProjectStatus,
        progress: Int,
        actualRoi: Double?,
    ): Result<Project> = runCatching {
        api.updateProjectProgress(
            id,
            ProjectUpdateProgressDto(status = status.name, progress = progress, actualRoi = actualRoi),
        ).toDomain()
    }

    override suspend fun deleteProject(id: String): Result<Unit> = runCatching {
        api.deleteProject(id)
    }
}