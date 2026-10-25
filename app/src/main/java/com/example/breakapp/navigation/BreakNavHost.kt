package com.example.breakapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.breakapp.main.BreakMainScreen
import com.example.breakapp.main.cart.CartScreen
import com.example.breakapp.main.cart.CartViewModel
import com.example.breakapp.main.cart.PaymentErrorScreen
import com.example.breakapp.main.cart.PaymentErrorType
import com.example.breakapp.main.data.PaymentMethod
import com.example.breakapp.main.data.SessionManager
import com.example.breakapp.main.orders.OrderConfirmationScreen
import com.example.breakapp.main.orders.OrderTrackingScreen
import com.example.breakapp.main.restaurant.RestaurantDetailScreen
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
    const val REGISTRO = "com/example/breakapp/ui/registro"

    // Módulo 2: Perfil y Configuración
    const val PERFIL = "perfil"
    const val EDITAR_PERFIL = "editar_perfil"
    const val METODOS_PAGO = "metodos_pago"
    const val CONFIGURACION = "configuracion"
    const val IDIOMA = "idioma"
    const val NOTIFICACIONES = "notificaciones"
    const val PRIVACIDAD = "privacidad"
    const val AYUDA_SOPORTE = "ayuda_soporte"

    // Módulo 3: Pantallas principales (Inicio, Restaurantes, Búsqueda)
    const val PRINCIPAL = "principal"

    // Módulo 4: Menú de Restaurante, Carrito y Pedidos
    const val RESTAURANTE_DETALLE = "restaurante_detalle/{restaurantId}?initialSearch={initialSearch}"
    const val CARRITO = "carrito"
    const val CONFIRMACION_PEDIDO = "confirmacion_pedido/{orderId}"
    const val SEGUIMIENTO_PEDIDO = "seguimiento_pedido/{orderId}"
    const val PAGO_RECHAZADO = "pago_rechazado"
    const val FONDOS_INSUFICIENTES = "fondos_insuficientes"

    fun restauranteDetalle(restaurantId: String, initialSearch: String? = null): String {
        return if (!initialSearch.isNullOrBlank()) {
            "restaurante_detalle/$restaurantId?initialSearch=" + java.net.URLEncoder.encode(initialSearch, "UTF-8")
        } else {
            "restaurante_detalle/$restaurantId"
        }
    }
    fun confirmacionPedido(orderId: String) = "confirmacion_pedido/$orderId"
    fun seguimientoPedido(orderId: String) = "seguimiento_pedido/$orderId"
}

@Composable
fun BreakNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // ViewModel compartido para todo el flujo de perfil
    val perfilViewModel: PerfilViewModel = viewModel()
    val perfilState by perfilViewModel.uiState.collectAsStateWithLifecycle()

    // ViewModel compartido para todo el flujo del carrito y pagos
    val cartViewModel: CartViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = modifier
    ) {
        // --- FLUJO MÓDULO 1 ---
        composable(Rutas.SPLASH) {
            val context = LocalContext.current
            SplashScreen(
                onTiempoCumplido = {
                    // Si el usuario guardó su sesión y sigue autenticado, ingresa directo a la pantalla principal
                    if (SessionManager.haySesionActiva(context)) {
                        perfilViewModel.cargarDatosUsuario()
                        navController.navigate(Rutas.PRINCIPAL) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Rutas.BIENVENIDA) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
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
                    // Después del login el usuario llega a Inicio (módulo 3)
                    navController.navigate(Rutas.PRINCIPAL) {
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
            val context = LocalContext.current
            RegistroRoute(
                onIrALogin = {
                    // Al registrarse con éxito, guardamos la sesión, recargamos los datos y pasamos directo a Inicio
                    SessionManager.guardarSesion(context, recordar = true)
                    perfilViewModel.cargarDatosUsuario()
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- FLUJO MÓDULO 3 (INICIO, RESTAURANTES, BÚSQUEDA) ---
        composable(Rutas.PRINCIPAL) {
            BreakMainScreen(
                cartViewModel = cartViewModel,
                onProfileClick = { navController.navigate(Rutas.PERFIL) },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(Rutas.restauranteDetalle(restaurantId))
                },
                onProductClick = { product ->
                    navController.navigate(Rutas.restauranteDetalle(product.restaurantId, product.name))
                },
                onOrderConfirmed = { order ->
                    navController.navigate(Rutas.confirmacionPedido(order.id))
                },
                onInsufficientFunds = {
                    navController.navigate(Rutas.FONDOS_INSUFICIENTES)
                },
                onCardDeclined = {
                    navController.navigate(Rutas.PAGO_RECHAZADO)
                }
            )
        }

        // --- FLUJO MÓDULO 4 (MENÚ RESTAURANTE, CARRITO, PEDIDOS) ---
        composable(
            route = Rutas.RESTAURANTE_DETALLE,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.StringType },
                navArgument("initialSearch") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            val initialSearch = backStackEntry.arguments?.getString("initialSearch")
            RestaurantDetailScreen(
                restaurantId = restaurantId,
                initialSearch = initialSearch,
                onBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Rutas.CARRITO) }
            )
        }

        composable(Rutas.CARRITO) {
            CartScreen(
                viewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onOrderConfirmed = { order ->
                    navController.navigate(Rutas.confirmacionPedido(order.id)) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = false }
                    }
                },
                onInsufficientFunds = {
                    navController.navigate(Rutas.FONDOS_INSUFICIENTES)
                },
                onCardDeclined = {
                    navController.navigate(Rutas.PAGO_RECHAZADO)
                }
            )
        }

        composable(Rutas.PAGO_RECHAZADO) {
            PaymentErrorScreen(
                errorType = PaymentErrorType.RECHAZADO,
                onPrimaryAction = {
                    // Intentar de nuevo: vuelve al carrito para reintentar o corregir datos
                    navController.popBackStack()
                },
                onSecondaryAction = {
                    // Cambiar método de pago: selecciona Efectivo o Nequi y regresa al carrito
                    cartViewModel.selectPaymentMethod(PaymentMethod.CASH_NEQUI)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Rutas.FONDOS_INSUFICIENTES) {
            PaymentErrorScreen(
                errorType = PaymentErrorType.FONDOS_INSUFICIENTES,
                onPrimaryAction = {
                    // Usar otro método de pago: selecciona Efectivo o Nequi y regresa al carrito
                    cartViewModel.selectPaymentMethod(PaymentMethod.CASH_NEQUI)
                    navController.popBackStack()
                },
                onSecondaryAction = {
                    // Cancelar pedido: regresa al inicio
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.CONFIRMACION_PEDIDO,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderConfirmationScreen(
                orderId = orderId,
                onNavigateToTracking = {
                    navController.navigate(Rutas.seguimientoPedido(orderId)) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Rutas.SEGUIMIENTO_PEDIDO,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            OrderTrackingScreen(
                onBack = {
                    navController.navigate(Rutas.PRINCIPAL) {
                        popUpTo(Rutas.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }

        // --- FLUJO MÓDULO 2 (PERFIL Y CONFIGURACIÓN) ---
        composable(Rutas.PERFIL) {
            val context = LocalContext.current
            PerfilScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onNavigateToEditarPerfil = { navController.navigate(Rutas.EDITAR_PERFIL) },
                onNavigateToMetodosPago = { navController.navigate(Rutas.METODOS_PAGO) },
                onNavigateToConfiguracion = { navController.navigate(Rutas.CONFIGURACION) },
                onCerrarSesionCompleto = {
                    SessionManager.cerrarSesion(context)
                    perfilViewModel.limpiarSesion()
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
            val context = LocalContext.current
            ConfiguracionScreen(
                state = perfilState,
                onEvent = perfilViewModel::onEvent,
                onVolver = { navController.popBackStack() },
                onNavigateToIdioma = { navController.navigate(Rutas.IDIOMA) },
                onNavigateToNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) },
                onNavigateToPrivacidad = { navController.navigate(Rutas.PRIVACIDAD) },
                onNavigateToAyuda = { navController.navigate(Rutas.AYUDA_SOPORTE) },
                onCuentaEliminada = {
                    SessionManager.cerrarSesion(context)
                    perfilViewModel.limpiarSesion()
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
