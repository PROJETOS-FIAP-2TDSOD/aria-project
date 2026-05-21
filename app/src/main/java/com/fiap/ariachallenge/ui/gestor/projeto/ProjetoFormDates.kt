package com.fiap.ariachallenge.ui.gestor.projeto

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun formatProjetoMediumDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).format(date)
}

fun LocalDateTime.toEpochMillisAtStartOfDay(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun indexOfLabel(labels: List<String>, value: String): Int =
    labels.indexOf(value).let { if (it >= 0) it else 0 }
