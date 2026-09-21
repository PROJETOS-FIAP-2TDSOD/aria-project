package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.InMemoryApiStore
import com.fiap.ariachallenge.data.remote.OrientationEnricher
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toDto
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class FakeOrientationRepository @Inject constructor(
    private val store: InMemoryApiStore,
    private val api: AriaApiService,
) : IOrientationRepository {

    override fun getAllOrientations(): Flow<List<Orientation>> =
        store.orientations.map { list -> list.map { enrich(it) } }

    override fun getOrientationById(id: String): Flow<Orientation?> = flow {
        delay(300)
        val orientation = store.orientations.value.find { it.id == id }
        emit(orientation?.let { enrich(it) })
    }

    private fun enrich(orientation: Orientation): Orientation =
        OrientationEnricher.enrich(orientation, store.ideas.value, store.projects.value)

    override suspend fun createOrientation(orientation: Orientation): Result<Orientation> {
        delay(500)
        return runCatching {
            val created = orientation.copy(
                id = UUID.randomUUID().toString(),
                createdAt = LocalDateTime.now(),
            )
            api.createOrientation(created.toDto()).toDomain()
        }
    }

    override suspend fun updateOrientation(orientation: Orientation): Result<Orientation> {
        delay(400)
        return runCatching {
            api.updateOrientation(orientation.id, orientation.toDto()).toDomain()
        }
    }

    override suspend fun deleteOrientation(id: String): Result<Unit> {
        delay(300)
        return runCatching {
            api.deleteOrientation(id)
        }
    }
}
