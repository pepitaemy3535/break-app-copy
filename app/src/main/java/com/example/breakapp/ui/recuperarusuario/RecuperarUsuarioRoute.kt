package com.example.breakapp.ui.recuperarusuario

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Conecta el ViewModel con las pantallas del flujo "¿Olvidaste tu usuario?".
 *
 * @param onSalir sale del flujo y vuelve al login (X de pizza en el primer paso, atrás del sistema).
 */
@Composable
fun RecuperarUsuarioRoute(
    onSalir: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecuperarUsuarioViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // En el primer paso y en el final se sale al login; en los demás se retrocede un paso.
    val alVolver: () -> Unit = {
        if (state.paso == PasoRecuperacion.Correo || state.paso == PasoRecuperacion.Exito) {
            onSalir()
        } else {
            viewModel.onVolver()
        }
    }

    BackHandler(onBack = alVolver)

    RecuperarUsuarioScreen(
        state = state,
        onCorreoChange = viewModel::onCorreoChange,
        onCodigoChange = viewModel::onCodigoChange,
        onNuevoUsuarioChange = viewModel::onNuevoUsuarioChange,
        onConfirmarUsuarioChange = viewModel::onConfirmarUsuarioChange,
        onSiguiente = viewModel::onSiguiente,
        onIntentarDeNuevo = viewModel::onIntentarDeNuevo,
        onReenviarCodigo = viewModel::onReenviarCodigo,
        onVolver = alVolver,
        onIrAIniciarSesion = onSalir,
        modifier = modifier
    )
}
