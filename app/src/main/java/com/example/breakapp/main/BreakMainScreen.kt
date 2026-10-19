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
import com.example.breakapp.main.restaurant.RestaurantsScreen
import com.example.breakapp.main.search.SearchScreen
import com.example.breakapp.main.theme.BreakColors

import com.example.breakapp.main.cart.CartScreen
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.orders.OrdersScreen

import com.example.breakapp.main.data.Product

/**
 * Contenedor de las pantallas principales (lo que ve el usuario después de iniciar sesión):
 * Inicio, Cafeterías y Restaurantes, Búsqueda, Carrito, Pedidos + la barra inferior.
 *
 * @param onProfileClick se llama al tocar "Perfil".
 * @param onRestaurantClick se llama al tocar un restaurante.
 * @param onProductClick se llama al tocar un producto.
 * @param onOrderConfirmed se llama cuando se crea un pedido.
 */
@Composable
fun BreakMainScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onRestaurantClick: (String) -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onOrderConfirmed: (Order) -> Unit = {},
    onInsufficientFunds: (Order) -> Unit = {},
    onCardDeclined: (Order) -> Unit = {}
) {
    var tab by rememberSaveable { mutableStateOf(MainTab.INICIO) }
    var showRestaurants by rememberSaveable { mutableStateOf(false) }
    var focusSearchOnOpen by rememberSaveable { mutableStateOf(false) }

    BreakSystemBars()

    // Atrás: Restaurantes -> Inicio, Búsqueda -> Inicio, Carrito -> Inicio, Pedidos -> Inicio.
    BackHandler(enabled = tab == MainTab.INICIO && showRestaurants) { showRestaurants = false }
    BackHandler(enabled = tab == MainTab.BUSCAR) { tab = MainTab.INICIO }
    BackHandler(enabled = tab == MainTab.CARRITO) { tab = MainTab.INICIO }
    BackHandler(enabled = tab == MainTab.PEDIDOS) { tab = MainTab.INICIO }

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
                        RestaurantsScreen(onTiendaClick = onRestaurantClick)
                    } else {
                        HomeScreen(
                            onSearchClick = {
                                focusSearchOnOpen = true
                                tab = MainTab.BUSCAR
                            },
                            onSeeAllRestaurants = { showRestaurants = true },
                            onRestaurantClick = { rest -> onRestaurantClick(rest.id) },
                            onProductClick = { product -> onProductClick(product) }
                        )
                    }

                MainTab.BUSCAR -> SearchScreen(
                    autoFocus = focusSearchOnOpen,
                    onRestaurantClick = onRestaurantClick,
                    onProductClick = onProductClick
                )

                MainTab.CARRITO -> CartScreen(
                    onBack = { tab = MainTab.INICIO },
                    onOrderConfirmed = onOrderConfirmed,
                    onInsufficientFunds = onInsufficientFunds,
                    onCardDeclined = onCardDeclined
                )

                MainTab.PEDIDOS -> OrdersScreen(
                    onBackToHome = { tab = MainTab.INICIO }
                )

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

                    MainTab.CARRITO -> tab = MainTab.CARRITO

                    MainTab.PEDIDOS -> tab = MainTab.PEDIDOS

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