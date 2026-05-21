package com.fiap.ariachallenge.data.mock

import com.fiap.ariachallenge.domain.model.Notification
import com.fiap.ariachallenge.domain.model.NotificationType
import java.time.LocalDateTime

object MockNotifications {

    val allNotifications = listOf(
        Notification(
            id = "n1",
            title = "Ideia aprovada!",
            message = "Sua ideia 'App de checklist digital para manutenção' foi aprovada pelo gestor Carlos Mendes.",
            type = NotificationType.IDEIA_APROVADA,
            isRead = false,
            relatedIdeaId = "i2",
            createdAt = LocalDateTime.now().minusHours(2)
        ),
        Notification(
            id = "n2",
            title = "Nova orientação estratégica",
            message = "O líder Roberto Alves publicou uma nova orientação: 'Foco Q3: Redução de Custos Operacionais'.",
            type = NotificationType.NOVA_ORIENTACAO,
            isRead = false,
            createdAt = LocalDateTime.now().minusDays(1)
        ),
        Notification(
            id = "n3",
            title = "Sua ideia está em análise",
            message = "'Sistema de gamificação para motoristas' entrou em análise. Aguarde o feedback do gestor.",
            type = NotificationType.IDEIA_EM_ANALISE,
            isRead = true,
            relatedIdeaId = "i1",
            createdAt = LocalDateTime.now().minusDays(2)
        ),
        Notification(
            id = "n4",
            title = "Comentário na sua ideia",
            message = "Carlos Mendes comentou na ideia 'Rota inteligente': 'Parabéns! Ideia transformadora.'",
            type = NotificationType.COMENTARIO,
            isRead = true,
            relatedIdeaId = "i3",
            createdAt = LocalDateTime.now().minusDays(3)
        ),
        Notification(
            id = "n5",
            title = "Projeto atualizado",
            message = "O projeto 'Rota Inteligente 2.0' avançou para 65% de conclusão.",
            type = NotificationType.PROJETO_ATUALIZADO,
            isRead = true,
            createdAt = LocalDateTime.now().minusDays(4)
        ),
        Notification(
            id = "n6",
            title = "Ideia rejeitada",
            message = "'Sistema de feedback pós-entrega via WhatsApp' foi rejeitada. Verifique o feedback do gestor.",
            type = NotificationType.IDEIA_REJEITADA,
            isRead = true,
            relatedIdeaId = "i6",
            createdAt = LocalDateTime.now().minusDays(5)
        ),
        Notification(
            id = "n7",
            title = "Bem-vindo ao ARIA!",
            message = "O sistema ARIA está disponível para você submeter suas ideias de inovação. Comece agora!",
            type = NotificationType.SISTEMA,
            isRead = true,
            createdAt = LocalDateTime.now().minusDays(30)
        )
    )

    fun getByUserId(userId: String) = allNotifications
    fun getUnreadCount(userId: String) = allNotifications.count { !it.isRead }
}
