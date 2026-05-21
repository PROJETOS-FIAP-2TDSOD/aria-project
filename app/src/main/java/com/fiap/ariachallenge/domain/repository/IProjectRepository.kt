package com.fiap.ariachallenge.domain.repository

import kotlinx.coroutines.flow.Flow
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus

interface IProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectById(id: String): Flow<Project?>
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>>
    suspend fun createProject(project: Project): Result<Project>
    suspend fun updateProject(project: Project): Result<Project>
    suspend fun deleteProject(id: String): Result<Unit>
}
