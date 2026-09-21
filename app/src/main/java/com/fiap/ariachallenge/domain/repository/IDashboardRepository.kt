package com.fiap.ariachallenge.domain.repository

import com.fiap.ariachallenge.domain.model.DashboardSummary
import com.fiap.ariachallenge.domain.model.ProjectRoiSummary
import com.fiap.ariachallenge.domain.model.StrategyRoiSummary

interface IDashboardRepository {
    suspend fun getSummary(): Result<DashboardSummary>
    suspend fun getRoiByProject(): Result<List<ProjectRoiSummary>>
    suspend fun getRoiByStrategy(): Result<List<StrategyRoiSummary>>
}