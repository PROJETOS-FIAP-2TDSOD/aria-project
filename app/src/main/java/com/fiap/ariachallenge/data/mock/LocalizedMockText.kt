package com.fiap.ariachallenge.data.mock

import android.content.res.Configuration
import java.util.Locale

internal data class LocalizedMockText(
    val en: String,
    val pt: String,
    val es: String,
) {
    fun resolve(configuration: Configuration): String {
        val locale: Locale = configuration.locales[0]
        return when (locale.language.lowercase()) {
            "pt" -> pt
            "es" -> es
            else -> en
        }
    }
}
