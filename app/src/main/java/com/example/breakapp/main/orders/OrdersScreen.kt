package com.example.breakapp.main.orders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit = {},
    viewModel: OrdersViewModel = viewModel()
) {
    OrderTrackingScreen(
        onBack = onBackToHome,
        modifier = modifier,
        viewModel = viewModel
    )
}
