package com.fiap.ariachallenge.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fiap.ariachallenge.ui.operador.detalhes_ideia.DetalhesIdeiaScreen
import com.fiap.ariachallenge.ui.operador.home.HomeOperadorScreen
import com.fiap.ariachallenge.ui.operador.ideias.MinhasIdeiasScreen
import com.fiap.ariachallenge.ui.operador.notificacoes.NotificacoesScreen
import com.fiap.ariachallenge.ui.operador.nova_ideia.NovaIdeiaScreen
import com.fiap.ariachallenge.ui.lider.detalhes_orientacao.DetalhesOrientacaoLiderScreen
import com.fiap.ariachallenge.ui.operador.perfil.PerfilOperadorScreen

fun NavGraphBuilder.operadorNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    navigation(
        startDestination = AriaDestination.OperadorHome.route,
        route = "operador_graph"
    ) {
        composable(AriaDestination.OperadorHome.route) {
            HomeOperadorScreen(
                currentRoute = AriaDestination.OperadorHome.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateToNovaIdeia = { navController.navigate(AriaDestination.OperadorNovaIdeia.route) },
                onNavigateToIdeiaDetalhes = { navController.navigate(AriaDestination.OperadorDetalhesIdeia.createRoute(it)) },
                onNavigateToOrientacaoDetalhes = {
                    navController.navigate(AriaDestination.OperadorDetalhesOrientacao.createRoute(it))
                },
            )
        }

        composable(
            route = AriaDestination.OperadorDetalhesOrientacao.route,
            arguments = listOf(navArgument("orientationId") { type = NavType.StringType }),
        ) {
            DetalhesOrientacaoLiderScreen(
                readOnly = true,
                onBack = { navController.popBackStack() },
                onEdit = {},
                onIdeaClick = { ideaId ->
                    navController.navigate(AriaDestination.OperadorDetalhesIdeia.createRoute(ideaId))
                },
            )
        }

        composable(AriaDestination.OperadorIdeias.route) {
            MinhasIdeiasScreen(
                currentRoute = AriaDestination.OperadorIdeias.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onNavigateToDetalhes = { navController.navigate(AriaDestination.OperadorDetalhesIdeia.createRoute(it)) },
                onNavigateToNovaIdeia = { navController.navigate(AriaDestination.OperadorNovaIdeia.route) }
            )
        }

        composable(AriaDestination.OperadorNovaIdeia.route) {
            NovaIdeiaScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = AriaDestination.OperadorDetalhesIdeia.route,
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType })
        ) {
            DetalhesIdeiaScreen(onBack = { navController.popBackStack() })
        }

        composable(AriaDestination.OperadorNotificacoes.route) {
            NotificacoesScreen(
                currentRoute = AriaDestination.OperadorNotificacoes.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(AriaDestination.OperadorPerfil.route) {
            PerfilOperadorScreen(
                currentRoute = AriaDestination.OperadorPerfil.route,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } },
                onLogout = onLogout
            )
        }
    }
}
