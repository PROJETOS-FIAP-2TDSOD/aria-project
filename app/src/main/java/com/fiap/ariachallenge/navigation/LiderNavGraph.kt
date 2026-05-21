package com.fiap.ariachallenge.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fiap.ariachallenge.ui.lider.analises.AnaliseScreen
import com.fiap.ariachallenge.ui.lider.criar_orientacao.CriarOrientacaoScreen
import com.fiap.ariachallenge.ui.lider.dashboard.DashboardLiderScreen
import com.fiap.ariachallenge.ui.lider.detalhes_orientacao.DetalhesOrientacaoLiderScreen
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.lider.orientacoes.OrientacoesLiderScreen
import com.fiap.ariachallenge.ui.lider.perfil.PerfilLiderScreen
import com.fiap.ariachallenge.ui.lider.projetos.ProjetosLiderScreen
import com.fiap.ariachallenge.ui.lider.tendencias.TendenciasScreen
import com.fiap.ariachallenge.ui.gestor.criar_projeto.CriarProjetoScreen
import com.fiap.ariachallenge.ui.gestor.detalhes_projeto.DetalhesProjetoScreen
import com.fiap.ariachallenge.ui.gestor.editar_projeto.EditarProjetoScreen
import com.fiap.ariachallenge.ui.operador.detalhes_ideia.DetalhesIdeiaScreen
import com.fiap.ariachallenge.ui.operador.notificacoes.NotificacoesScreen

fun NavGraphBuilder.liderNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
) {
    navigation(
        startDestination = AriaDestination.LiderDashboard.route,
        route = "lider_graph",
    ) {
        composable(AriaDestination.LiderDashboard.route) {
            DashboardLiderScreen(
                currentRoute = AriaDestination.LiderDashboard.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onProjectClick = { id -> navController.navigate(AriaDestination.LiderDetalhesProjeto.createRoute(id)) },
            )
        }

        composable(AriaDestination.LiderOrientacoes.route) {
            OrientacoesLiderScreen(
                currentRoute = AriaDestination.LiderOrientacoes.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onCreateClick = { navController.navigate(AriaDestination.LiderCriarOrientacao.route) },
                onOrientationClick = { id -> navController.navigate(AriaDestination.LiderDetalhesOrientacao.createRoute(id)) },
            )
        }

        composable(AriaDestination.LiderCriarOrientacao.route) {
            CriarOrientacaoScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = AriaDestination.LiderEditarOrientacao.route,
            arguments = listOf(navArgument("orientationId") { type = NavType.StringType }),
        ) {
            CriarOrientacaoScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = AriaDestination.LiderDetalhesOrientacao.route,
            arguments = listOf(navArgument("orientationId") { type = NavType.StringType }),
        ) {
            DetalhesOrientacaoLiderScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(AriaDestination.LiderEditarOrientacao.createRoute(id)) },
                onIdeaClick = { id -> navController.navigate(AriaDestination.LiderDetalhesIdeia.createRoute(id)) },
            )
        }

        composable(AriaDestination.LiderProjetos.route) {
            ProjetosLiderScreen(
                currentRoute = AriaDestination.LiderProjetos.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateToDetalhes = { id -> navController.navigate(AriaDestination.LiderDetalhesProjeto.createRoute(id)) },
                onNavigateToCriarProjeto = { navController.navigate(AriaDestination.LiderCriarProjeto.createRoute()) },
            )
        }

        composable(
            route = AriaDestination.LiderCriarProjeto.route,
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
            route = AriaDestination.LiderDetalhesProjeto.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
        ) {
            DetalhesProjetoScreen(
                onBack = { navController.popBackStack() },
                onEdit = {
                    val projectId = it.arguments?.getString("projectId").orEmpty()
                    navController.navigate(AriaDestination.LiderEditarProjeto.createRoute(projectId))
                },
            )
        }

        composable(
            route = AriaDestination.LiderEditarProjeto.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
        ) {
            EditarProjetoScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = AriaDestination.LiderDetalhesIdeia.route,
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType }),
        ) {
            DetalhesIdeiaScreen(onBack = { navController.popBackStack() })
        }

        composable(AriaDestination.LiderAnalises.route) {
            AnaliseScreen(
                currentRoute = AriaDestination.LiderAnalises.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
            )
        }

        composable(AriaDestination.LiderTendencias.route) {
            TendenciasScreen(
                currentRoute = AriaDestination.LiderTendencias.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onIdeaClick = { id -> navController.navigate(AriaDestination.LiderDetalhesIdeia.createRoute(id)) },
            )
        }

        composable(AriaDestination.LiderNotificacoes.route) {
            NotificacoesScreen(
                currentRoute = AriaDestination.LiderNotificacoes.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                bottomNavItems = liderBottomNavItems(),
                onBack = { navController.popBackStack() },
            )
        }

        composable(AriaDestination.LiderPerfil.route) {
            PerfilLiderScreen(
                currentRoute = AriaDestination.LiderPerfil.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onLogout = onLogout,
            )
        }
    }
}
