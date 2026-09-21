package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toRequestDto
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

@Singleton
class OrientationRepositoryImpl @Inject constructor(
    private val api: AriaApiService,
) : IOrientationRepository {

    override fun getAllOrientations(): Flow<List<Orientation>> = flow {
        emit(api.getOrientations().map { it.toDomain() })
    }.catch { emit(emptyList()) }

    override fun getOrientationById(id: String): Flow<Orientation?> = flow {
        emit(runCatching { api.getOrientationById(id).toDomain() }.getOrNull())
    }.catch { emit(null) }

    override suspend fun createOrientation(orientation: Orientation): Result<Orientation> = runCatching {
        api.createOrientation(orientation.toRequestDto()).toDomain()
    }

    override suspend fun updateOrientation(orientation: Orientation): Result<Orientation> = runCatching {
        api.updateOrientation(orientation.id, orientation.toRequestDto()).toDomain()
    }

    override suspend fun deleteOrientation(id: String): Result<Unit> = runCatching {
        api.deleteOrientation(id)
    }
}