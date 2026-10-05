package com.example.breakapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.breakapp.ui.login.LoginRoute
import com.example.breakapp.ui.recuperarusuario.RecuperarUsuarioRoute
import com.example.breakapp.ui.registro.RegistroRoute
import com.example.breakapp.ui.splash.SplashScreen
import com.example.breakapp.ui.welcome.WelcomeScreen

object Rutas {
    const val SPLASH = "splash"
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val RECUPERAR_USUARIO = "recuperar_usuario"
    const val REGISTRO = "registro"
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
            LoginRoute(
                onOlvidoUsuario = { navController.navigate(Rutas.RECUPERAR_USUARIO) },
                onRegistro = { navController.navigate(Rutas.REGISTRO) }
            )
        }
        composable(Rutas.RECUPERAR_USUARIO) {
            // Salir del flujo (X en el primer paso, atrás o "Ir a iniciar sesión") vuelve al login.
            RecuperarUsuarioRoute(
                onSalir = { navController.popBackStack(Rutas.LOGIN, inclusive = false) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroRoute(
                onIrALogin = { navController.popBackStack(Rutas.LOGIN, inclusive = false) }
            )
        }
    }
}