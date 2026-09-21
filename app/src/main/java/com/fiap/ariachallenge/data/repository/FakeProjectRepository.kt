package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.InMemoryApiStore
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toDto
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class FakeProjectRepository @Inject constructor(
    private val store: InMemoryApiStore,
    private val api: AriaApiService,
) : IProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> = store.projects

    override fun getProjectById(id: String): Flow<Project?> = flow {
        delay(300)
        emit(store.projects.value.find { it.id == id })
    }

    override fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = flow {
        delay(300)
        emit(store.projects.value.filter { it.status == status })
    }

    override suspend fun createProject(project: Project): Result<Project> {
        delay(500)
        return runCatching {
            val newProject = project.copy(
                id = UUID.randomUUID().toString(),
                updatedAt = LocalDateTime.now(),
            )
            api.createProject(newProject.toDto()).toDomain()
        }
    }

    override suspend fun updateProject(project: Project): Result<Project> {
        delay(400)
        return runCatching {
            val updated = project.copy(updatedAt = LocalDateTime.now())
            api.updateProject(project.id, updated.toDto()).toDomain()
        }
    }

    override suspend fun deleteProject(id: String): Result<Unit> {
        delay(400)
        return runCatching {
            api.deleteProject(id)
        }
    }
}
