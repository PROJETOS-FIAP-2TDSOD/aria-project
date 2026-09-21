package com.fiap.ariachallenge.data.repository

import com.fiap.ariachallenge.data.remote.AriaApiService
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.domain.model.DashboardSummary
import com.fiap.ariachallenge.domain.model.ProjectRoiSummary
import com.fiap.ariachallenge.domain.model.StrategyRoiSummary
import com.fiap.ariachallenge.domain.repository.IDashboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val api: AriaApiService,
) : IDashboardRepository {

    override suspend fun getSummary(): Result<DashboardSummary> = runCatching {
        api.getDashboardSummary().toDomain()
    }

    override suspend fun getRoiByProject(): Result<List<ProjectRoiSummary>> = runCatching {
        api.getDashboardRoiByProject().map { it.toDomain() }
    }

    override suspend fun getRoiByStrategy(): Result<List<StrategyRoiSummary>> = runCatching {
        api.getDashboardRoiByStrategy().map { it.toDomain() }
    }
}