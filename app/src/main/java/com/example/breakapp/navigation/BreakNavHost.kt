package com.example.breakapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.breakapp.ui.login.LoginRoute
import com.example.breakapp.ui.olvidocontrasena.OlvidoContrasenaRoute
import com.example.breakapp.ui.perfil.ConfiguracionScreen
import com.example.breakapp.ui.perfil.EditarPerfilScreen
import com.example.breakapp.ui.perfil.MetodosPagoScreen
import com.example.breakapp.ui.perfil.PerfilScreen
import com.example.breakapp.ui.perfil.PerfilViewModel
import com.example.breakapp.ui.perfil.subpantallas.AyudaSoporteScreen
import com.example.breakapp.ui.perfil.subpantallas.IdiomaScreen
import com.example.breakapp.ui.perfil.subpantallas.NotificacionesScreen
import com.example.breakapp.ui.perfil.subpantallas.PrivacidadScreen
import com.example.breakapp.ui.recuperarusuario.RecuperarUsuarioRoute
import com.example.breakapp.ui.registro.RegistroRoute
import com.example.breakapp.ui.splash.SplashScreen
import com.example.breakapp.ui.welcome.WelcomeScreen

object Rutas {
    // Módulo 1: Autenticación y Recuperación
    const val SPLASH = "splash"
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val RECUPERAR_USUARIO = "recuperar_usuario"
    const val OLVIDO_CONTRASENA = "olvido_contrasena"
    const val REGISTRO = "registro"

    // Módulo 2: Perfil y Configuración
    const val PERFIL = "perfil"
    const val EDITAR_PERFIL = "editar_perfil"
    const val METODOS_PAGO = "metodos_pago"
    const val CONFIGURACION = "configuracion"
    const val IDIOMA = "idioma"
    const val NOTIFICACIONES = "notificaciones"
    const val PRIVACIDAD = "privacidad"
    const val AYUDA_SOPORTE = "ayuda_soporte"
}

@Composable
fun BreakNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // ViewModel compartido para todo el flujo de perfil
    val perfilViewModel: PerfilViewModel = viewModel()
    val perfilState by perfilViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = modifier
    ) {
        // --- FLUJO MÓDULO 1 ---
        composable(Rutas.SPLASH) {
            SplashScreen(
                onTiempoCumplido = {
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
                onOlvidoContrasena = { navController.navigate(Rutas.OLVIDO_CONTRASENA) },
                onRegistro = { navController.navigate(Rutas.REGISTRO) },
                onIngresoExitoso = {
                    perfilViewModel.cargarDatosUsuario()
                    navController.navigate(Rutas.PERFIL) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.RECUPERAR_USUARIO) {
            RecuperarUsuarioRoute(
                onSalir = { navController.popBackStack(Rutas.LOGIN, inclusive = false) }
            )
        }
        composable(Rutas.OLVIDO_CONTRASENA) {
            OlvidoContrasenaRoute(
                onSalir = { navController.popBackStack(Rutas.LOGIN, inclusive = false) },
                onIrALogin = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroRoute(
                onIrALogin = { navController.popBackStack(Rutas.LOGIN, inclusive = false) }
            )
        }

        // --- FLUJO MÓDULO 2 (PERFIL Y CONFIGURACIÓN) ---
        composable(Rutas.PERFIL) {
            PerfilScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onNavigateToEditarPerfil = { navController.navigate(Rutas.EDITAR_PERFIL) },
                onNavigateToMetodosPago = { navController.navigate(Rutas.METODOS_PAGO) },
                onNavigateToConfiguracion = { navController.navigate(Rutas.CONFIGURACION) },
                onCerrarSesionCompleto = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.EDITAR_PERFIL) {
            EditarPerfilScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.METODOS_PAGO) {
            MetodosPagoScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.CONFIGURACION) {
            ConfiguracionScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() },
                onNavigateToIdioma = { navController.navigate(Rutas.IDIOMA) },
                onNavigateToNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) },
                onNavigateToPrivacidad = { navController.navigate(Rutas.PRIVACIDAD) },
                onNavigateToAyuda = { navController.navigate(Rutas.AYUDA_SOPORTE) },
                onCuentaEliminada = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.IDIOMA) {
            IdiomaScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.PRIVACIDAD) {
            PrivacidadScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.AYUDA_SOPORTE) {
            AyudaSoporteScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}