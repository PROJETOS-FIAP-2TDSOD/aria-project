package com.fiap.ariachallenge.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

val DefaultMoneyLocale: Locale = Locale("pt", "BR")

private fun currencyFormatter(locale: Locale = DefaultMoneyLocale): NumberFormat =
    NumberFormat.getCurrencyInstance(locale)

private fun decimalSymbols(locale: Locale = DefaultMoneyLocale): DecimalFormatSymbols =
    DecimalFormatSymbols(locale)

private fun compactNumber(value: Double, pattern: String, locale: Locale = DefaultMoneyLocale): String =
    DecimalFormat(pattern, decimalSymbols(locale)).format(value)

fun formatCurrencyBrl(valueReais: Double, locale: Locale = DefaultMoneyLocale): String =
    currencyFormatter(locale).format(valueReais.coerceAtLeast(0.0))

fun formatCurrencyCompact(valueReais: Double, locale: Locale = DefaultMoneyLocale): String {
    if (valueReais <= 0.0) return formatCurrencyBrl(0.0, locale)
    val absVal = abs(valueReais)
    return when {
        absVal >= 1_000_000 -> {
            val scaled = absVal / 1_000_000.0
            val num = compactNumber(scaled, "#,##0.#", locale)
                .trimEnd('0')
                .trimEnd(decimalSymbols(locale).decimalSeparator)
            "R$ ${num}M"
        }
        absVal >= 1_000 -> {
            val scaled = absVal / 1_000.0
            val num = if (scaled % 1.0 < 0.05) {
                compactNumber(scaled, "#,##0", locale)
            } else {
                compactNumber(scaled, "#,##0.#", locale)
                    .trimEnd('0')
                    .trimEnd(decimalSymbols(locale).decimalSeparator)
            }
            "R$ ${num}k"
        }
        else -> formatCurrencyBrl(valueReais, locale)
    }
}

data class CurrencyDisplayParts(
    val symbol: String = "R$",
    val amount: String,
    val suffix: String = "",
)

fun formatCurrencyDisplayParts(
    valueReais: Double,
    locale: Locale = DefaultMoneyLocale,
): CurrencyDisplayParts {
    val compact = formatCurrencyCompact(valueReais, locale)
    if (valueReais < 1_000) {
        return CurrencyDisplayParts(amount = compact.removePrefix("R$").trim())
    }
    val withoutSymbol = compact.removePrefix("R$").trim()
    val suffix = when {
        withoutSymbol.endsWith("M", ignoreCase = true) -> "M"
        withoutSymbol.endsWith("k", ignoreCase = true) -> "k"
        else -> ""
    }
    val amount = if (suffix.isNotEmpty()) {
        withoutSymbol.dropLast(suffix.length).trim()
    } else {
        withoutSymbol
    }
    return CurrencyDisplayParts(amount = amount, suffix = suffix)
}
