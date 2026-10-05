package com.example.breakapp.ui.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R

/** Conecta el ViewModel con la pantalla de login. */
@Composable
fun LoginRoute(
    onOlvidoUsuario: () -> Unit,
    onRegistro: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    LaunchedEffect(state.ingresoExitoso) {
        if (state.ingresoExitoso) {
            // TEMPORAL: aún no existe la pantalla principal en el diseño.
            Toast.makeText(contexto, R.string.login_toast_temporal, Toast.LENGTH_SHORT).show()
            viewModel.ingresoConsumido()
        }
    }

    LoginScreen(
        state = state,
        onUsuarioChange = viewModel::onUsuarioChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onIngresar = viewModel::onIngresar,
        // El enlace dice "¿Olvidaste tu usuario?" solo cuando el error es de usuario.
        // TODO: el caso "¿Olvidaste tu contraseña?" se conecta cuando llegue ese flujo.
        onOlvido = {
            if (state.error == ErrorLogin.Usuario) onOlvidoUsuario()
        },
        onRegistro = onRegistro,
        modifier = modifier
    )
}