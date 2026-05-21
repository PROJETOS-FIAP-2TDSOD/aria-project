package com.fiap.ariachallenge.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.ui.auth.login.LoginScreen
import com.fiap.ariachallenge.ui.auth.recover.RecoverPasswordScreen
import com.fiap.ariachallenge.ui.auth.register.RegisterScreen
import com.fiap.ariachallenge.ui.splash.SplashScreen
import com.fiap.ariachallenge.ui.theme.AriaTheme

private fun NavHostController.navigateToRole(role: UserRole, popRoute: String) {
    val dest = when (role) {
        UserRole.OPERADOR -> "operador_graph"
        UserRole.GESTOR -> "gestor_graph"
        UserRole.LIDER -> "lider_graph"
    }
    navigate(dest) { popUpTo(popRoute) { inclusive = true } }
}

private const val NavAnimMs = 280

@Composable
fun AriaNavGraph(navController: NavHostController) {
    val bg = AriaTheme.colors.bgPrimary
    val fade = tween<Float>(NavAnimMs, easing = FastOutSlowInEasing)
    NavHost(
        navController = navController,
        startDestination = AriaDestination.Splash.route,
        modifier = Modifier.fillMaxSize().background(bg),
        enterTransition = { fadeIn(fade) },
        exitTransition = { fadeOut(fade) },
        popEnterTransition = { fadeIn(fade) },
        popExitTransition = { fadeOut(fade) },
    ) {
        composable(AriaDestination.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(AriaDestination.Login.route) {
                        popUpTo(AriaDestination.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = { role ->
                    navController.navigateToRole(role, AriaDestination.Splash.route)
                }
            )
        }

        composable(AriaDestination.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    navController.navigateToRole(role, AriaDestination.Login.route)
                },
                onNavigateToRecover = {
                    navController.navigate(AriaDestination.RecoverPassword.route)
                },
                onNavigateToRegister = {
                    navController.navigate(AriaDestination.Register.route)
                },
            )
        }

        composable(AriaDestination.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegistered = { role ->
                    navController.navigateToRole(role, AriaDestination.Login.route)
                },
            )
        }

        composable(AriaDestination.RecoverPassword.route) {
            RecoverPasswordScreen(onBack = { navController.popBackStack() })
        }

        operadorNavGraph(navController, onLogout = {
            navController.navigate(AriaDestination.Login.route) {
                popUpTo("operador_graph") { inclusive = true }
            }
        })

        gestorNavGraph(navController, onLogout = {
            navController.navigate(AriaDestination.Login.route) {
                popUpTo("gestor_graph") { inclusive = true }
            }
        })

        liderNavGraph(navController, onLogout = {
            navController.navigate(AriaDestination.Login.route) {
                popUpTo("lider_graph") { inclusive = true }
            }
        })
    }
}
