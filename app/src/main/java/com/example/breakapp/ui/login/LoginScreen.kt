package com.example.breakapp.ui.login

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.BordeAdvertencia
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BordeValido
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo
import com.example.breakapp.ui.theme.GrisTitulo
import com.example.breakapp.ui.theme.Morado

@Composable
fun LoginScreen(
    state: LoginUiState,
    onUsuarioChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onIngresar: () -> Unit,
    modifier: Modifier = Modifier,
    onOlvido: () -> Unit = {},   // TODO: pantalla de recuperación (aún sin diseño enviado)
    onGoogle: () -> Unit = {},   // TODO: Google Sign-In requiere dependencia aprobada
    onRegistro: () -> Unit = {}  // TODO: pantalla de registro (aún sin diseño enviado)
) {
    val focusManager = LocalFocusManager.current

    val bordeUsuario = when (state.error) {
        ErrorLogin.Ninguno -> BordeValido
        ErrorLogin.Usuario -> BordeError
        ErrorLogin.Contrasena -> BordeAdvertencia
    }
    val bordeContrasena = when (state.error) {
        ErrorLogin.Ninguno -> BordeValido
        ErrorLogin.Usuario -> BordeAdvertencia
        ErrorLogin.Contrasena -> BordeError
    }
    val textoEnlace = if (state.error == ErrorLogin.Usuario) {
        R.string.login_olvido_usuario
    } else {
        R.string.login_olvido_contrasena
    }
    val mensajeError: Int? = when (state.error) {
        ErrorLogin.Ninguno -> null
        ErrorLogin.Usuario -> R.string.login_error_usuario
        ErrorLogin.Contrasena -> R.string.login_error_contrasena
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
            .imePadding()
    ) {
        // La imagen se sale 16 dp por el borde derecho, como en el diseño.
        Image(
            painter = painterResource(R.drawable.img_pizza),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 16.dp)
                .width(122.dp)
                .height(67.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(67.dp))
            Text(
                text = stringResource(R.string.login_titulo),
                style = BreakText.TituloLogin,
                color = GrisTitulo,
                modifier = Modifier.padding(start = 42.dp)
            )
            Spacer(Modifier.height(94.dp))

            CampoLogin(
                etiqueta = stringResource(R.string.login_usuario_etiqueta),
                valor = state.usuario,
                onValorChange = onUsuarioChange,
                placeholder = stringResource(R.string.login_usuario_placeholder),
                icono = R.drawable.ic_usuario,
                colorBorde = bordeUsuario,
                teclado = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                )
            )
            Spacer(Modifier.height(14.dp))
            CampoLogin(
                etiqueta = stringResource(R.string.login_contrasena_etiqueta),
                valor = state.contrasena,
                onValorChange = onContrasenaChange,
                placeholder = stringResource(R.string.login_contrasena_placeholder),
                icono = R.drawable.ic_candado,
                colorBorde = bordeContrasena,
                enmascarado = true,
                teclado = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false
                ),
                acciones = KeyboardActions(onDone = { enviar(focusManager, onIngresar) })
            )

            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(textoEnlace),
                style = BreakText.Enlace,
                color = Ambar,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 33.dp)
                    .clickable(onClick = onOlvido)
                    .padding(vertical = 4.dp)
            )

            Spacer(Modifier.height(9.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(start = 39.dp)
            ) {
                if (mensajeError != null) {
                    Text(
                        text = stringResource(mensajeError),
                        style = BreakText.Cuerpo,
                        color = Color.White,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            }

            BotonGoogle(onClick = onGoogle)

            Spacer(Modifier.height(77.dp))
            BotonPrimario(
                texto = stringResource(R.string.login_ingresar),
                onClick = { enviar(focusManager, onIngresar) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(204.dp)
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.login_registro),
                style = BreakText.Enlace,
                color = Ambar,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onRegistro)
                    .padding(vertical = 4.dp)
            )

            Spacer(Modifier.height(29.dp))
            PieUniversidad()
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun enviar(focusManager: FocusManager, onIngresar: () -> Unit) {
    focusManager.clearFocus()
    onIngresar()
}

@Composable
private fun CampoLogin(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes icono: Int,
    colorBorde: Color,
    teclado: KeyboardOptions,
    modifier: Modifier = Modifier,
    enmascarado: Boolean = false,
    acciones: KeyboardActions = KeyboardActions.Default
) {
    val forma = RoundedCornerShape(10.dp)
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 33.dp)) {
        Text(
            text = etiqueta,
            style = BreakText.Etiqueta,
            color = Color.White,
            modifier = Modifier.height(45.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(forma)
                .background(Color.White)
                .border(4.dp, colorBorde, forma)
                .padding(start = 29.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(icono), contentDescription = null)
            Spacer(Modifier.width(13.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                singleLine = true,
                textStyle = BreakText.Campo.copy(color = Morado),
                cursorBrush = SolidColor(Morado),
                visualTransformation = if (enmascarado) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = teclado,
                keyboardActions = acciones,
                modifier = Modifier.weight(1f),
                decorationBox = { campoInterno ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (valor.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = BreakText.Campo,
                                color = Morado.copy(alpha = 0.6f)
                            )
                        }
                        campoInterno()
                    }
                }
            )
        }
    }
}

@Composable
private fun BotonGoogle(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 33.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_google_continuar),
            contentDescription = stringResource(R.string.login_google),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun LoginPreview() {
    BreakAppTheme {
        LoginScreen(LoginUiState(), onUsuarioChange = {}, onContrasenaChange = {}, onIngresar = {})
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun LoginErrorUsuarioPreview() {
    BreakAppTheme {
        LoginScreen(
            LoginUiState(error = ErrorLogin.Usuario),
            onUsuarioChange = {}, onContrasenaChange = {}, onIngresar = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun LoginErrorContrasenaPreview() {
    BreakAppTheme {
        LoginScreen(
            LoginUiState(error = ErrorLogin.Contrasena),
            onUsuarioChange = {}, onContrasenaChange = {}, onIngresar = {}
        )
    }
}
