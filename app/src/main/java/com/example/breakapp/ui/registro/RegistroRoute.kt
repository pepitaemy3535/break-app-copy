package com.example.breakapp.ui.registro

import androidx.compose.runtime.Composable

@Composable
fun RegistroRoute(
    onIrALogin: () -> Unit
) {
    RegistroScreen(
        onIrALogin = onIrALogin
    )
}