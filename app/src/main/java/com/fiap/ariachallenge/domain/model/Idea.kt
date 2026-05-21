package com.fiap.ariachallenge.domain.model

import androidx.annotation.StringRes
import com.fiap.ariachallenge.R
import java.time.LocalDateTime

data class Idea(
    val id: String,
    val title: String,
    val author: User,
    val category: IdeaCategory,
    val description: String,
    val problema: String,
    val beneficios: String,
    val recursos: String,
    val status: IdeaStatus,
    val score: Int? = null,
    val gestorFeedback: String? = null,
    val estimatedRoi: Double? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class IdeaStatus(val displayName: String) {
    AGUARDANDO_ANALISE("Aguardando"),
    EM_ANALISE("Em Análise"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada"),
    EM_PROJETO("Em Projeto");

    @StringRes
    fun getDisplayNameRes(): Int = when (this) {
        AGUARDANDO_ANALISE -> R.string.idea_status_waiting
        EM_ANALISE         -> R.string.idea_status_in_analysis
        APROVADA           -> R.string.idea_status_approved
        REJEITADA          -> R.string.idea_status_rejected
        EM_PROJETO         -> R.string.idea_status_in_project
    }
}

enum class IdeaCategory(val displayName: String) {
    TECNOLOGIA("Tecnologia"),
    PROCESSO("Processo"),
    PRODUTO("Produto"),
    PESSOAS("Pessoas"),
    SUSTENTABILIDADE("Sustentabilidade"),
    SEGURANCA("Segurança"),
    LOGISTICA("Logística"),
    FINANCEIRO("Financeiro"),
    ATENDIMENTO("Atendimento");

    @StringRes
    fun getDisplayNameRes(): Int = when (this) {
        TECNOLOGIA       -> R.string.idea_category_technology
        PROCESSO         -> R.string.idea_category_process
        PRODUTO          -> R.string.idea_category_product
        PESSOAS          -> R.string.idea_category_people
        SUSTENTABILIDADE -> R.string.idea_category_sustainability
        SEGURANCA        -> R.string.idea_category_safety
        LOGISTICA        -> R.string.idea_category_logistics
        FINANCEIRO       -> R.string.idea_category_financial
        ATENDIMENTO      -> R.string.idea_category_service
    }
}
