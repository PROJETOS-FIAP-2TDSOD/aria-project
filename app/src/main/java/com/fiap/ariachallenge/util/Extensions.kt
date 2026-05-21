package com.fiap.ariachallenge.util

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun LocalDateTime.toTimeAgo(): String {
    val now = LocalDateTime.now()
    val minutes = ChronoUnit.MINUTES.between(this, now)
    val hours = ChronoUnit.HOURS.between(this, now)
    val days = ChronoUnit.DAYS.between(this, now)
    val months = ChronoUnit.MONTHS.between(this, now)

    return when {
        minutes < 1 -> "agora mesmo"
        minutes < 60 -> "há $minutes min"
        hours < 24 -> "há ${hours}h"
        days < 7 -> "há $days dia${if (days > 1) "s" else ""}"
        days < 30 -> "há ${days / 7} semana${if (days / 7 > 1) "s" else ""}"
        months < 12 -> "há $months mês${if (months > 1) "es" else ""}"
        else -> "há ${months / 12} ano${if (months / 12 > 1) "s" else ""}"
    }
}

fun Double.toRoiBRL(): String = formatCurrencyBrl(this)
