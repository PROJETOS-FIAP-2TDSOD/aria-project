package com.fiap.ariachallenge.navigation

sealed class AriaDestination(val route: String) {
    object Splash : AriaDestination("splash")
    object Login : AriaDestination("login")
    object Register : AriaDestination("register")
    object RecoverPassword : AriaDestination("recover_password")

    // Operador
    object OperadorHome : AriaDestination("operador/home")
    object OperadorIdeias : AriaDestination("operador/ideias")
    object OperadorNovaIdeia : AriaDestination("operador/nova_ideia")
    object OperadorDetalhesIdeia : AriaDestination("operador/ideias/{ideaId}") {
        fun createRoute(ideaId: String) = "operador/ideias/$ideaId"
    }
    object OperadorNotificacoes : AriaDestination("operador/notificacoes")
    object OperadorDetalhesOrientacao : AriaDestination("operador/orientacoes/{orientationId}") {
        fun createRoute(orientationId: String) = "operador/orientacoes/$orientationId"
    }
    object OperadorPerfil : AriaDestination("operador/perfil")

    // Gestor
    object GestorHome : AriaDestination("gestor/home")
    object GestorPendentes : AriaDestination("gestor/pendentes")
    object GestorAnalisar : AriaDestination("gestor/analisar/{ideaId}") {
        fun createRoute(ideaId: String) = "gestor/analisar/$ideaId"
    }
    object GestorProjetos : AriaDestination("gestor/projetos")
    object GestorCriarProjeto : AriaDestination("gestor/criar_projeto?ideaId={ideaId}") {
        fun createRoute(ideaId: String? = null) =
            if (ideaId.isNullOrBlank()) "gestor/criar_projeto?ideaId="
            else "gestor/criar_projeto?ideaId=$ideaId"
    }
    object GestorDetalhesProjeto : AriaDestination("gestor/projetos/{projectId}") {
        fun createRoute(projectId: String) = "gestor/projetos/$projectId"
    }
    object GestorEditarProjeto : AriaDestination("gestor/projetos/{projectId}/editar") {
        fun createRoute(projectId: String) = "gestor/projetos/$projectId/editar"
    }
    object GestorOrientacoes : AriaDestination("gestor/orientacoes")
    object GestorDetalhesOrientacao : AriaDestination("gestor/orientacoes/{orientationId}") {
        fun createRoute(orientationId: String) = "gestor/orientacoes/$orientationId"
    }
    object GestorNotificacoes : AriaDestination("gestor/notificacoes")
    object GestorPerfil : AriaDestination("gestor/perfil")

    // Lider
    object LiderDashboard : AriaDestination("lider/dashboard")
    object LiderOrientacoes : AriaDestination("lider/orientacoes")
    object LiderCriarOrientacao : AriaDestination("lider/criar_orientacao")
    object LiderDetalhesOrientacao : AriaDestination("lider/orientacoes/{orientationId}") {
        fun createRoute(orientationId: String) = "lider/orientacoes/$orientationId"
    }
    object LiderAnalises : AriaDestination("lider/analises")
    object LiderTendencias : AriaDestination("lider/tendencias")
    object LiderProjetos : AriaDestination("lider/projetos")
    object LiderDetalhesProjeto : AriaDestination("lider/projetos/{projectId}") {
        fun createRoute(projectId: String) = "lider/projetos/$projectId"
    }
    object LiderCriarProjeto : AriaDestination("lider/criar_projeto?ideaId={ideaId}") {
        fun createRoute(ideaId: String? = null) =
            if (ideaId.isNullOrBlank()) "lider/criar_projeto?ideaId="
            else "lider/criar_projeto?ideaId=$ideaId"
    }
    object LiderEditarProjeto : AriaDestination("lider/projetos/{projectId}/editar") {
        fun createRoute(projectId: String) = "lider/projetos/$projectId/editar"
    }
    object LiderDetalhesIdeia : AriaDestination("lider/ideias/{ideaId}") {
        fun createRoute(ideaId: String) = "lider/ideias/$ideaId"
    }
    object LiderEditarOrientacao : AriaDestination("lider/orientacoes/{orientationId}/editar") {
        fun createRoute(orientationId: String) = "lider/orientacoes/$orientationId/editar"
    }
    object LiderNotificacoes : AriaDestination("lider/notificacoes")
    object LiderPerfil : AriaDestination("lider/perfil")
}
