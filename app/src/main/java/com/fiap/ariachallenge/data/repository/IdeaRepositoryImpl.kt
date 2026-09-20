package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.dto.IdeaReviewDto
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toRequestDto
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class IdeaRepositoryImpl @Inject constructor(
    private val api: AriaApiService,
) : IIdeaRepository {

    override fun getAllIdeas(): Flow<List<Idea>> = flow {
        emit(api.getIdeas().map { it.toDomain() })
    }

    override fun getIdeaById(id: String): Flow<Idea?> = flow {
        emit(runCatching { api.getIdeaById(id).toDomain() }.getOrNull())
    }

    override fun getIdeasByAuthor(authorId: String): Flow<List<Idea>> = flow {
        emit(api.getIdeas().map { it.toDomain() }.filter { it.author.id == authorId })
    }

    override fun getIdeasByStatus(status: IdeaStatus): Flow<List<Idea>> = flow {
        emit(api.getIdeas().map { it.toDomain() }.filter { it.status == status })
    }

    override fun getPendingIdeas(): Flow<List<Idea>> = flow {
        emit(
            api.getIdeas().map { it.toDomain() }
                .filter { it.status == IdeaStatus.AGUARDANDO_ANALISE || it.status == IdeaStatus.EM_ANALISE }
                .sortedByDescending { it.createdAt },
        )
    }

    override suspend fun submitIdea(idea: Idea): Result<Idea> = runCatching {
        api.createIdea(idea.toRequestDto()).toDomain()
    }

    override suspend fun updateIdea(idea: Idea): Result<Idea> = runCatching {
        api.updateIdea(idea.id, idea.toRequestDto()).toDomain()
    }

    override suspend fun reviewIdea(
        id: String,
        status: IdeaStatus,
        score: Int?,
        gestorFeedback: String?,
    ): Result<Idea> = runCatching {
        api.reviewIdea(id, IdeaReviewDto(status = status.name, score = score, gestorFeedback = gestorFeedback))
            .toDomain()
    }

    override suspend fun scoreIdeaWithAi(id: String): Result<Idea> = runCatching {
        api.scoreIdeaWithAi(id).toDomain()
    }

    override suspend fun deleteIdea(id: String): Result<Unit> = runCatching {
        api.deleteIdea(id)
    }
}