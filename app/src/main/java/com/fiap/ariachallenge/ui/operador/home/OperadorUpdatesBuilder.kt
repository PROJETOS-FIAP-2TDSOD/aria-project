package com.fiap.ariachallenge.ui.operador.home

import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.util.toTimeAgo

fun buildOperadorRecentUpdates(ideas: List<Idea>, limit: Int = 3): List<OperadorIdeaUpdate> =
    ideas
        .sortedByDescending { it.updatedAt }
        .take(limit)
        .map { idea ->
            OperadorIdeaUpdate(
                ideaId = idea.id,
                title = idea.title,
                status = idea.status,
                meta = idea.updatedAt.toTimeAgo(),
            )
        }
