package com.example.breakapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.breakapp.ui.login.LoginRoute
import com.example.breakapp.ui.splash.SplashScreen
import com.example.breakapp.ui.welcome.WelcomeScreen

object Rutas {
    const val SPLASH = "splash"
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
}

@Composable
fun BreakNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = modifier
    ) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onTiempoCumplido = {
                    // El splash se quita de la pila: "atrás" no vuelve a él.
                    navController.navigate(Rutas.BIENVENIDA) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.BIENVENIDA) {
            WelcomeScreen(onSiguiente = { navController.navigate(Rutas.LOGIN) })
        }
        composable(Rutas.LOGIN) {
            LoginRoute()
        }
    }
}
