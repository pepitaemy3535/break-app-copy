package com.example.breakapp.ui.recuperarusuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.CampoBreak
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.BordeAdvertencia
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BordeValido
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo

/**
 * Pantallas "Forgott user 1 a 5" del Figma (correo, código y nuevo usuario) y la pantalla final.
 * Las 5 primeras comparten el mismo marco: pizza, título, perro y botón.
 */
@Composable
fun RecuperarUsuarioScreen(
    state: RecuperarUsuarioUiState,
    onCorreoChange: (String) -> Unit,
    onCodigoChange: (String) -> Unit,
    onNuevoUsuarioChange: (String) -> Unit,
    onConfirmarUsuarioChange: (String) -> Unit,
    onSiguiente: () -> Unit,
    onIntentarDeNuevo: () -> Unit,
    onReenviarCodigo: () -> Unit,
    onVolver: () -> Unit,
    onIrAIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.paso == PasoRecuperacion.Exito) {
        UsuarioActualizado(onIrAIniciarSesion = onIrAIniciarSesion, modifier = modifier)
        return
    }

    val focusManager = LocalFocusManager.current
    val avanzar: () -> Unit = {
        focusManager.clearFocus()
        onSiguiente()
    }

    val enError = state.errorCorreo || state.errorUsuarios
    val textoBoton = if (enError) R.string.recuperar_intentar_nuevo else R.string.recuperar_siguiente
    val anchoBoton = if (enError) 250.dp else 204.dp
    val accionBoton: () -> Unit = if (enError) onIntentarDeNuevo else avanzar

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
            .imePadding()
    ) {
        PizzaEsquina(
            onClick = onVolver,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(53.dp))
            Text(
                text = stringResource(R.string.recuperar_titulo),
                style = BreakText.TituloRecuperacion,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 34.dp)
                    .widthIn(max = 320.dp)
            )
            Spacer(Modifier.height(10.dp))
            // El perro mide 315 x 236 dp en el diseño; se escala si la pantalla es más angosta.
            Image(
                painter = painterResource(R.drawable.img_cabito),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 90.dp, end = 7.dp)
                    .aspectRatio(315f / 236f)
            )

            // Zona del paso: va desde el perro hasta el botón (325 dp en el diseño).
            Column(modifier = Modifier.heightIn(min = 325.dp)) {
                when (state.paso) {
                    PasoRecuperacion.Correo -> PasoCorreo(
                        state = state,
                        onCorreoChange = onCorreoChange,
                        onListo = avanzar
                    )

                    PasoRecuperacion.Codigo -> PasoCodigo(
                        state = state,
                        onCodigoChange = onCodigoChange,
                        onReenviarCodigo = onReenviarCodigo,
                        onListo = avanzar
                    )

                    PasoRecuperacion.NuevoUsuario -> PasoNuevoUsuario(
                        state = state,
                        onNuevoUsuarioChange = onNuevoUsuarioChange,
                        onConfirmarUsuarioChange = onConfirmarUsuarioChange,
                        onListo = avanzar
                    )

                    PasoRecuperacion.Exito -> Unit
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                BotonPrimario(
                    texto = stringResource(textoBoton),
                    onClick = accionBoton,
                    modifier = Modifier.width(anchoBoton)
                )
            }

            Spacer(Modifier.height(39.dp))
            PieUniversidad()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PasoCorreo(
    state: RecuperarUsuarioUiState,
    onCorreoChange: (String) -> Unit,
    onListo: () -> Unit
) {
    Spacer(Modifier.height(26.dp))
    TextoGuia(texto = stringResource(R.string.recuperar_correo_instruccion))
    Spacer(Modifier.height(57.dp))
    CampoBreak(
        etiqueta = stringResource(R.string.recuperar_correo_etiqueta),
        valor = state.correo,
        onValorChange = onCorreoChange,
        placeholder = stringResource(R.string.recuperar_correo_placeholder),
        icono = R.drawable.ic_correo,
        colorBorde = if (state.errorCorreo) BordeError else BordeValido,
        habilitado = !state.errorCorreo,
        teclado = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),
        acciones = KeyboardActions(onDone = { onListo() })
    )
    if (state.errorCorreo) {
        Spacer(Modifier.height(15.dp))
        MensajeError(texto = stringResource(R.string.recuperar_correo_error))
    }
}

@Composable
private fun PasoCodigo(
    state: RecuperarUsuarioUiState,
    onCodigoChange: (String) -> Unit,
    onReenviarCodigo: () -> Unit,
    onListo: () -> Unit
) {
    Spacer(Modifier.height(24.dp))
    TextoGuia(texto = stringResource(R.string.recuperar_codigo_instruccion))
    Spacer(Modifier.height(55.dp))
    CampoBreak(
        etiqueta = stringResource(R.string.recuperar_codigo_etiqueta),
        valor = state.codigo,
        onValorChange = onCodigoChange,
        placeholder = stringResource(R.string.recuperar_codigo_placeholder),
        icono = R.drawable.ic_codigo,
        colorBorde = BordeAdvertencia,
        teclado = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),
        acciones = KeyboardActions(onDone = { onListo() })
    )
    Spacer(Modifier.height(17.dp))
    Text(
        text = stringResource(R.string.recuperar_reenviar_codigo),
        style = BreakText.TextoGuia,
        color = Color.White,
        modifier = Modifier
            .padding(start = 34.dp)
            .clickable(role = Role.Button, onClick = onReenviarCodigo)
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun PasoNuevoUsuario(
    state: RecuperarUsuarioUiState,
    onNuevoUsuarioChange: (String) -> Unit,
    onConfirmarUsuarioChange: (String) -> Unit,
    onListo: () -> Unit
) {
    val borde = if (state.errorUsuarios) BordeError else BordeValido
    Spacer(Modifier.height(49.dp))
    CampoBreak(
        etiqueta = stringResource(R.string.recuperar_nuevo_etiqueta),
        valor = state.nuevoUsuario,
        onValorChange = onNuevoUsuarioChange,
        placeholder = stringResource(R.string.recuperar_usuario_placeholder),
        icono = R.drawable.ic_usuario,
        colorBorde = borde,
        habilitado = !state.errorUsuarios,
        teclado = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            autoCorrectEnabled = false
        )
    )
    Spacer(Modifier.height(29.dp))
    CampoBreak(
        etiqueta = stringResource(R.string.recuperar_confirmar_etiqueta),
        valor = state.confirmarUsuario,
        onValorChange = onConfirmarUsuarioChange,
        placeholder = stringResource(R.string.recuperar_usuario_placeholder),
        icono = R.drawable.ic_usuario,
        colorBorde = borde,
        habilitado = !state.errorUsuarios,
        teclado = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),
        acciones = KeyboardActions(onDone = { onListo() })
    )
    if (state.errorUsuarios) {
        Spacer(Modifier.height(13.dp))
        MensajeError(texto = stringResource(R.string.recuperar_error_usuarios))
    }
}

/** Pantalla "Forgott user 6": usuario actualizado. */
@Composable
private fun UsuarioActualizado(
    onIrAIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
    ) {
        // Aquí la pizza es solo decorativa (en el diseño no hay botón de volver).
        PizzaEsquina(onClick = null, modifier = Modifier.align(Alignment.TopEnd))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(57.dp))
            Text(
                text = stringResource(R.string.recuperar_exito_titulo),
                style = BreakText.TituloExito,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(57.dp))
            Image(
                painter = painterResource(R.drawable.img_check_verde),
                contentDescription = stringResource(R.string.recuperar_exito_check_descripcion),
                modifier = Modifier.size(300.dp)
            )
            Text(
                text = stringResource(R.string.recuperar_exito_mensaje),
                style = BreakText.Cuerpo,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 282.dp)
            )
            Spacer(Modifier.height(100.dp))
            BotonPrimario(
                texto = stringResource(R.string.recuperar_ir_a_login),
                onClick = onIrAIniciarSesion,
                modifier = Modifier.width(304.dp)
            )
            Spacer(Modifier.height(33.dp))
            PieUniversidad()
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Pizza de la esquina: en los pasos del flujo hace de botón para volver. */
@Composable
private fun PizzaEsquina(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    // La imagen se sale 16 dp por el borde derecho, como en el diseño.
    val base = modifier
        .offset(x = 16.dp)
        .width(122.dp)
        .height(67.dp)
    val conClic = if (onClick != null) base.clickable(role = Role.Button, onClick = onClick) else base
    Image(
        painter = painterResource(R.drawable.img_pizza),
        contentDescription = if (onClick != null) stringResource(R.string.recuperar_volver_descripcion) else null,
        contentScale = ContentScale.Crop,
        modifier = conClic
    )
}

@Composable
private fun TextoGuia(texto: String) {
    Text(
        text = texto,
        style = BreakText.TextoGuia,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 30.dp)
    )
}

@Composable
private fun MensajeError(texto: String) {
    Text(
        text = texto,
        style = BreakText.MensajeError,
        color = BordeError,
        modifier = Modifier
            .padding(horizontal = 37.dp)
            .semantics { liveRegion = LiveRegionMode.Polite }
    )
}

// ---------- Vistas previas (una por pantalla del Figma) ----------

@Composable
private fun PrevisualizarPantalla(state: RecuperarUsuarioUiState) {
    BreakAppTheme {
        RecuperarUsuarioScreen(
            state = state,
            onCorreoChange = {}, onCodigoChange = {},
            onNuevoUsuarioChange = {}, onConfirmarUsuarioChange = {},
            onSiguiente = {}, onIntentarDeNuevo = {}, onReenviarCodigo = {},
            onVolver = {}, onIrAIniciarSesion = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PreviewCorreo() = PrevisualizarPantalla(RecuperarUsuarioUiState())

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PreviewCorreoError() =
    PrevisualizarPantalla(RecuperarUsuarioUiState(errorCorreo = true))

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PreviewCodigo() =
    PrevisualizarPantalla(RecuperarUsuarioUiState(paso = PasoRecuperacion.Codigo))

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PreviewNuevoUsuario() =
    PrevisualizarPantalla(RecuperarUsuarioUiState(paso = PasoRecuperacion.NuevoUsuario))

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PreviewUsuariosError() = PrevisualizarPantalla(
    RecuperarUsuarioUiState(paso = PasoRecuperacion.NuevoUsuario, errorUsuarios = true)
)

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PreviewExito() =
    PrevisualizarPantalla(RecuperarUsuarioUiState(paso = PasoRecuperacion.Exito))
