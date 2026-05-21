package com.fiap.ariachallenge.util

import com.fiap.ariachallenge.domain.model.OrientationKeyMetric

object OrientationKeyMetricParser {

    fun fromText(text: String): List<OrientationKeyMetric> =
        text.lines()
            .map { it.trim().removePrefix("•").trim() }
            .filter { it.isNotBlank() }
            .map(::parseLine)

    fun toText(metrics: List<OrientationKeyMetric>): String =
        metrics.joinToString("\n") { metric ->
            "• ${metric.name} · ${metric.achieved} / ${metric.target}"
        }

    private fun parseLine(line: String): OrientationKeyMetric {
        val separators = listOf('·', '/', '|')
        var name = line
        var achieved = "0"
        var target = "0"

        for (separator in separators) {
            if (line.contains(separator)) {
                val parts = line.split(separator).map { it.trim() }.filter { it.isNotBlank() }
                if (parts.size >= 2) {
                    name = parts[0]
                    achieved = parts[1]
                    target = parts.getOrElse(2) { parts[1] }
                }
                break
            }
        }

        val progress = computeProgress(achieved, target)
        return OrientationKeyMetric(
            name = name,
            achieved = achieved,
            target = target,
            progress = progress,
        )
    }

    fun computeProgress(achieved: String, target: String): Float {
        val achievedValue = achieved.extractNumber() ?: return 0f
        val targetValue = target.extractNumber() ?: return 0f
        if (targetValue <= 0.0) return 0f
        return (achievedValue / targetValue).toFloat().coerceIn(0f, 1f)
    }

    private fun String.extractNumber(): Double? {
        val normalized = replace(',', '.')
        val match = Regex("""[\d.]+""").find(normalized)?.value ?: return null
        return match.toDoubleOrNull()
    }
}
