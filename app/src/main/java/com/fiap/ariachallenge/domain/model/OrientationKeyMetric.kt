package com.fiap.ariachallenge.domain.model

data class OrientationKeyMetric(
    val name: String,
    val achieved: String,
    val target: String,
    val progress: Float = 0f,
)
