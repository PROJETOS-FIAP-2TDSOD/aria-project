package com.fiap.ariachallenge.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fiap.ariachallenge.ui.gestor.analisar.AnalisarIdeiaScreen
import com.fiap.ariachallenge.ui.gestor.criar_projeto.CriarProjetoScreen
import com.fiap.ariachallenge.ui.gestor.detalhes_projeto.DetalhesProjetoScreen
import com.fiap.ariachallenge.ui.gestor.editar_projeto.EditarProjetoScreen
import com.fiap.ariachallenge.ui.gestor.gestorBottomNavItems
import com.fiap.ariachallenge.ui.gestor.home.HomeGestorScreen
import com.fiap.ariachallenge.ui.gestor.orientacoes.OrientacoesGestorScreen
import com.fiap.ariachallenge.ui.lider.detalhes_orientacao.DetalhesOrientacaoLiderScreen
import com.fiap.ariachallenge.ui.gestor.pendentes.PendentesScreen
import com.fiap.ariachallenge.ui.gestor.perfil.PerfilGestorScreen
import com.fiap.ariachallenge.ui.gestor.projetos.ProjetosScreen
import com.fiap.ariachallenge.ui.operador.notificacoes.NotificacoesScreen

fun NavGraphBuilder.gestorNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    navigation(
        startDestination = AriaDestination.GestorHome.route,
        route = "gestor_graph"
    ) {
        composable(AriaDestination.GestorHome.route) {
            HomeGestorScreen(
                currentRoute = AriaDestination.GestorHome.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateToAnalisar = { navController.navigate(AriaDestination.GestorAnalisar.createRoute(it)) },
                onNavigateToDetalhesProjeto = { navController.navigate(AriaDestination.GestorDetalhesProjeto.createRoute(it)) },
                onNavigateToDetalhesOrientacao = { navController.navigate(AriaDestination.GestorDetalhesOrientacao.createRoute(it)) },
            )
        }

        composable(AriaDestination.GestorPendentes.route) {
            PendentesScreen(
                currentRoute = AriaDestination.GestorPendentes.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateToAnalisar = { navController.navigate(AriaDestination.GestorAnalisar.createRoute(it)) }
            )
        }

        composable(
            route = AriaDestination.GestorAnalisar.route,
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType })
        ) {
            AnalisarIdeiaScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCriarProjeto = { ideaId ->
                    navController.navigate(AriaDestination.GestorCriarProjeto.createRoute(ideaId)) {
                        popUpTo(AriaDestination.GestorPendentes.route) { inclusive = false }
                    }
                },
            )
        }

        composable(AriaDestination.GestorProjetos.route) {
            ProjetosScreen(
                currentRoute = AriaDestination.GestorProjetos.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateToCriarProjeto = { navController.navigate(AriaDestination.GestorCriarProjeto.createRoute()) },
                onNavigateToDetalhesProjeto = { navController.navigate(AriaDestination.GestorDetalhesProjeto.createRoute(it)) }
            )
        }

        composable(
            route = AriaDestination.GestorCriarProjeto.route,
            arguments = listOf(
                navArgument("ideaId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            CriarProjetoScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = AriaDestination.GestorDetalhesProjeto.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
        ) {
            DetalhesProjetoScreen(
                onBack = { navController.popBackStack() },
                onEdit = {
                    val projectId = it.arguments?.getString("projectId").orEmpty()
                    navController.navigate(AriaDestination.GestorEditarProjeto.createRoute(projectId))
                },
            )
        }

        composable(
            route = AriaDestination.GestorEditarProjeto.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
        ) {
            EditarProjetoScreen(
                onBack = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack()
                    navController.popBackStack()
                },
            )
        }

        composable(AriaDestination.GestorOrientacoes.route) {
            OrientacoesGestorScreen(
                currentRoute = AriaDestination.GestorOrientacoes.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onOrientationClick = { id -> navController.navigate(AriaDestination.GestorDetalhesOrientacao.createRoute(id)) },
            )
        }

        composable(
            route = AriaDestination.GestorDetalhesOrientacao.route,
            arguments = listOf(navArgument("orientationId") { type = NavType.StringType }),
        ) {
            DetalhesOrientacaoLiderScreen(
                readOnly = true,
                onBack = { navController.popBackStack() },
                onEdit = {},
                onIdeaClick = { ideaId -> navController.navigate(AriaDestination.GestorAnalisar.createRoute(ideaId)) },
            )
        }

        composable(AriaDestination.GestorNotificacoes.route) {
            NotificacoesScreen(
                currentRoute = AriaDestination.GestorNotificacoes.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                bottomNavItems = gestorBottomNavItems(),
                onBack = { navController.popBackStack() },
            )
        }

        composable(AriaDestination.GestorPerfil.route) {
            PerfilGestorScreen(
                currentRoute = AriaDestination.GestorPerfil.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onLogout = onLogout
            )
        }
    }
}
