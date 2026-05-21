package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.InMemoryApiStore
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toDto
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class FakeIdeaRepository @Inject constructor(
    private val store: InMemoryApiStore,
    private val api: AriaApiService,
) : IIdeaRepository {

    override fun getAllIdeas(): Flow<List<Idea>> = store.ideas

    override fun getIdeaById(id: String): Flow<Idea?> = flow {
        delay(300)
        emit(store.ideas.value.find { it.id == id })
    }

    override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flow {
        delay(300)
        emit(store.ideas.value.filter { it.author.id == authorId })
    }

    override fun getIdeasByStatus(status: IdeaStatus): Flow<List<Idea>> = flow {
        delay(300)
        emit(store.ideas.value.filter { it.status == status })
    }

    override fun getPendingIdeas(): Flow<List<Idea>> = flow {
        delay(300)
        emit(
            store.ideas.value.filter {
                it.status == IdeaStatus.AGUARDANDO_ANALISE || it.status == IdeaStatus.EM_ANALISE
            }.sortedByDescending { it.createdAt },
        )
    }

    override suspend fun submitIdea(idea: Idea): Result<Idea> {
        delay(400)
        return runCatching {
            val newIdea = idea.copy(
                id = UUID.randomUUID().toString(),
                status = IdeaStatus.AGUARDANDO_ANALISE,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
            api.createIdea(newIdea.toDto()).toDomain()
        }
    }

    override suspend fun updateIdea(idea: Idea): Result<Idea> {
        delay(400)
        return runCatching {
            val updated = idea.copy(updatedAt = LocalDateTime.now())
            api.updateIdea(idea.id, updated.toDto()).toDomain()
        }
    }

    override suspend fun deleteIdea(id: String): Result<Unit> {
        delay(300)
        return runCatching {
            api.deleteIdea(id)
        }
    }
}
