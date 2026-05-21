package com.fiap.ariachallenge.ui.operador.home

import com.fiap.ariachallenge.domain.model.IdeaStatus

data class OperadorIdeaUpdate(
    val ideaId: String,
    val title: String,
    val status: IdeaStatus,
    val meta: String? = null,
)
