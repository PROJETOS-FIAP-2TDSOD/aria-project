package com.fiap.ariachallenge.ui.lider.criar_orientacao

import com.fiap.ariachallenge.domain.model.OrientationKeyMetric
import com.fiap.ariachallenge.util.OrientationKeyMetricParser
import java.util.UUID

data class KeyMetricFormItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val achieved: String = "",
    val target: String = "",
) {
    fun toDomain(): OrientationKeyMetric {
        val progress = OrientationKeyMetricParser.computeProgress(achieved, target)
        return OrientationKeyMetric(
            name = name.trim(),
            achieved = achieved.trim(),
            target = target.trim(),
            progress = progress,
        )
    }

    companion object {
        fun fromDomain(metric: OrientationKeyMetric): KeyMetricFormItem = KeyMetricFormItem(
            name = metric.name,
            achieved = metric.achieved,
            target = metric.target,
        )

        fun empty(): KeyMetricFormItem = KeyMetricFormItem()
    }
}

fun List<KeyMetricFormItem>.toDomainMetrics(): List<OrientationKeyMetric> =
    filter { it.name.isNotBlank() || it.achieved.isNotBlank() || it.target.isNotBlank() }
        .map { it.toDomain() }
