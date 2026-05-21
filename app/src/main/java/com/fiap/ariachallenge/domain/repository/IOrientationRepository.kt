package com.fiap.ariachallenge.domain.repository

import kotlinx.coroutines.flow.Flow
import com.fiap.ariachallenge.domain.model.Orientation

interface IOrientationRepository {
    fun getAllOrientations(): Flow<List<Orientation>>
    fun getOrientationById(id: String): Flow<Orientation?>
    suspend fun createOrientation(orientation: Orientation): Result<Orientation>
    suspend fun updateOrientation(orientation: Orientation): Result<Orientation>
    suspend fun deleteOrientation(id: String): Result<Unit>
}
