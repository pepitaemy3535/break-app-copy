package com.example.breakapp.ui.olvidocontrasena

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.ui.recuperacion.PasoCodigoScreen
import com.example.breakapp.ui.recuperacion.PasoCorreoScreen
import com.example.breakapp.ui.recuperacion.RecuperacionExitoScreen

/** Decide qué paso mostrar y conecta el ViewModel con las pantallas. */
@Composable
fun OlvidoContrasenaRoute(
    onSalir: () -> Unit,
    onIrALogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OlvidoContrasenaViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    val atras: () -> Unit = {
        when {
            state.paso == PasoContrasena.Exito -> onIrALogin()
            !viewModel.retrocederPaso() -> onSalir()
            else -> Unit
        }
    }
    BackHandler(onBack = atras)

    when (state.paso) {
        PasoContrasena.Correo -> PasoCorreoScreen(
            titulo = R.string.olvido_contrasena_titulo,
            correo = state.correo,
            enError = state.correoInvalido,
            onCorreoChange = viewModel::onCorreoChange,
            onSiguiente = viewModel::onCorreoSiguiente,
            onIntentarDeNuevo = viewModel::reintentarCorreo,
            onAtras = atras,
            modifier = modifier
        )
        PasoContrasena.Codigo -> PasoCodigoScreen(
            titulo = R.string.olvido_contrasena_titulo,
            codigo = state.codigo,
            codigoInvalido = state.codigoInvalido,
            onCodigoChange = viewModel::onCodigoChange,
            onSiguiente = viewModel::onCodigoSiguiente,
            onReenviar = {
                // TEMPORAL: aún no se envía ningún código real.
                Toast.makeText(contexto, R.string.olvido_codigo_reenviado_temporal, Toast.LENGTH_SHORT).show()
            },
            onAtras = atras,
            modifier = modifier
        )
        PasoContrasena.NuevaContrasena -> PasoNuevaContrasenaScreen(
            state = state,
            onNuevaContrasenaChange = viewModel::onNuevaContrasenaChange,
            onConfirmarContrasenaChange = viewModel::onConfirmarContrasenaChange,
            onGuardar = viewModel::onGuardar,
            onIntentarDeNuevo = viewModel::reintentarContrasenas,
            onAtras = atras,
            modifier = modifier
        )
        PasoContrasena.Exito -> RecuperacionExitoScreen(
            titulo = R.string.exito_contrasena_titulo,
            mensaje = R.string.exito_contrasena_mensaje,
            onIrAIniciarSesion = onIrALogin,
            modifier = modifier
        )
    }
}
