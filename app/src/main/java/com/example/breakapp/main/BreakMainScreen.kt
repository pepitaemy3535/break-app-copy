package com.example.breakapp.main

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.breakapp.main.components.BreakBottomBar
import com.example.breakapp.main.components.MainTab
import com.example.breakapp.main.home.HomeScreen
import com.example.breakapp.main.home.RestaurantsScreen
import com.example.breakapp.main.search.SearchScreen
import com.example.breakapp.main.theme.BreakColors

/**
 * Contenedor de las pantallas principales (lo que ve el usuario después de iniciar sesión):
 * Inicio, Cafeterías y Restaurantes, Búsqueda + la barra inferior.
 *
 * @param onProfileClick se llama al tocar "Perfil". Aquí conectarás el módulo de Perfil.
 */
@Composable
fun BreakMainScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {}
) {
    var tab by rememberSaveable { mutableStateOf(MainTab.INICIO) }
    var showRestaurants by rememberSaveable { mutableStateOf(false) }
    var focusSearchOnOpen by rememberSaveable { mutableStateOf(false) }

    BreakSystemBars()

    // Atrás: Restaurantes -> Inicio, Búsqueda -> Inicio.
    BackHandler(enabled = tab == MainTab.INICIO && showRestaurants) { showRestaurants = false }
    BackHandler(enabled = tab == MainTab.BUSCAR) { tab = MainTab.INICIO }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            when (tab) {
                MainTab.INICIO ->
                    if (showRestaurants) {
                        RestaurantsScreen()
                    } else {
                        HomeScreen(
                            onSearchClick = {
                                focusSearchOnOpen = true
                                tab = MainTab.BUSCAR
                            },
                            onSeeAllRestaurants = { showRestaurants = true }
                        )
                    }

                MainTab.BUSCAR -> SearchScreen(autoFocus = focusSearchOnOpen)

                // Carrito, Pedidos y Perfil no abren pantalla aquí.
                else -> Unit
            }
        }

        BreakBottomBar(
            selected = tab,
            onTabClick = { target ->
                when (target) {
                    MainTab.INICIO -> {
                        tab = MainTab.INICIO
                        showRestaurants = false
                    }

                    MainTab.BUSCAR -> {
                        focusSearchOnOpen = false
                        tab = MainTab.BUSCAR
                    }

                    // Sin diseño todavía: el botón se ve pero no abre nada.
                    MainTab.CARRITO, MainTab.PEDIDOS -> Unit

                    MainTab.PERFIL -> onProfileClick()
                }
            }
        )
    }
}

/** Íconos claros en la barra de estado (fondo morado) y oscuros en la barra de navegación (blanca). */
@Composable
private fun BreakSystemBars() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = true
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
