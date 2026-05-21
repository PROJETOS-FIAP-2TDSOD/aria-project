package com.fiap.ariachallenge.domain.model

import androidx.annotation.StringRes
import com.fiap.ariachallenge.R
import java.time.LocalDateTime

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val relatedIdeaId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class NotificationType(val displayName: String) {
    IDEIA_APROVADA("Ideia Aprovada"),
    IDEIA_REJEITADA("Ideia Rejeitada"),
    IDEIA_EM_ANALISE("Em Análise"),
    NOVA_ORIENTACAO("Nova Orientação"),
    COMENTARIO("Comentário"),
    PROJETO_ATUALIZADO("Projeto Atualizado"),
    SISTEMA("Sistema");

    @StringRes
    fun getDisplayNameRes(): Int = when (this) {
        IDEIA_APROVADA    -> R.string.notification_type_idea_approved
        IDEIA_REJEITADA   -> R.string.notification_type_idea_rejected
        IDEIA_EM_ANALISE  -> R.string.notification_type_idea_in_analysis
        NOVA_ORIENTACAO   -> R.string.notification_type_new_orientation
        COMENTARIO        -> R.string.notification_type_feedback
        PROJETO_ATUALIZADO -> R.string.notification_type_project_update
        SISTEMA           -> R.string.notification_type_system
    }
}
