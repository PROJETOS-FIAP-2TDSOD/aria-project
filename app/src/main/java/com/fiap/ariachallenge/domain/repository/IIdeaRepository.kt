package com.fiap.ariachallenge.domain.repository

import kotlinx.coroutines.flow.Flow
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus

interface IIdeaRepository {
    fun getAllIdeas(): Flow<List<Idea>>
    fun getIdeaById(id: String): Flow<Idea?>
    fun getIdeasByAuthor(authorId: String): Flow<List<Idea>>
    fun getIdeasByStatus(status: IdeaStatus): Flow<List<Idea>>
    fun getPendingIdeas(): Flow<List<Idea>>
    suspend fun submitIdea(idea: Idea): Result<Idea>
    suspend fun updateIdea(idea: Idea): Result<Idea>
    suspend fun deleteIdea(id: String): Result<Unit>
}
