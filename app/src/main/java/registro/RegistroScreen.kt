package com.example.breakapp.ui.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.BreakAppTheme

// COLORES EXACTOS DEL HTML/CSS
val FondoCSS = Color(0xFF2C1462)
val BotonNaranja = Color(0xFFF59E0B)
val BordeDorado = Color(0xFFC79E1D)
val BordeRojo = Color(0xFFF03012)
val BordeVerdeExito = Color(0xFF2BFF20)
val TextoMorado = Color(0xFF8C00D6)
val TextoVerdeFuerte = Color(0xFF305F28)

@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = viewModel(),
    onIrALogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCSS)
            .systemBarsPadding()
            .imePadding()
    ) {
        when {
            state.registroExitoso -> PantallaExito(onIrALogin = onIrALogin)
            state.error == ErrorRegistro.ErrorServidor -> PantallaErrorServidor(
                onIntentarNuevo = { viewModel.reintentar() },
                onVolverRegistro = { viewModel.reintentar() }
            )
            else -> FormularioRegistro(state = state, viewModel = viewModel)
        }
    }
}

@Composable
fun FormularioRegistro(
    state: RegistroUIState,
    viewModel: RegistroViewModel
) {
    val hayError = state.error == ErrorRegistro.ContrasenasNoCoinciden
    val colorBordeContrasena = if (hayError) BordeRojo else BordeVerdeExito

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img_pizza),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 11.dp, y = (-4).dp)
                .width(122.dp)
                .height(67.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(55.dp))

            Text(
                text = "Regístrate",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE7E7E7),
                letterSpacing = (-1).sp,
                modifier = Modifier.padding(start = 23.dp)
            )

            Spacer(Modifier.height(25.dp))

            CampoExactoCSS(
                etiqueta = "Correo",
                valor = state.correo,
                onValorChange = viewModel::onCorreoChange,
                placeholder = "nombre@gmail.com",
                icono = R.drawable.ic_correo,
                colorBorde = BordeDorado,
                teclado = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(13.dp))

            CampoExactoCSS(
                etiqueta = "Usuario:",
                valor = state.usuario,
                onValorChange = viewModel::onUsuarioChange,
                placeholder = "@nombre_usuario",
                icono = R.drawable.ic_codigo,
                colorBorde = BordeDorado,
                teclado = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(13.dp))

            CampoExactoCSS(
                etiqueta = "Contraseña:",
                valor = state.contrasena,
                onValorChange = viewModel::onContrasenaChange,
                placeholder = "••••••••",
                icono = R.drawable.ic_candado,
                colorBorde = colorBordeContrasena,
                enmascarado = true,
                mostrarFuerte = true,
                teclado = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(13.dp))

            CampoExactoCSS(
                etiqueta = "Confirmar contraseña",
                valor = state.confirmarContrasena,
                onValorChange = viewModel::onConfirmarContrasenaChange,
                placeholder = "••••••••",
                icono = R.drawable.ic_candado,
                colorBorde = colorBordeContrasena,
                enmascarado = true,
                mostrarFuerte = true,
                teclado = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (hayError) "Las contraseñas no coinciden" else "Las contraseñas coinciden",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (hayError) BordeRojo else BordeVerdeExito,
                modifier = Modifier.padding(start = 39.dp)
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = { viewModel.registrarUsuario() },
                colors = ButtonDefaults.buttonColors(containerColor = BotonNaranja),
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(250.dp)
                    .height(59.dp)
            ) {
                Text(
                    text = if (hayError) "Intentar de nuevo" else "Siguiente",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(35.dp))
            PieUniversidad()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CampoExactoCSS(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icono: Int,
    colorBorde: Color,
    teclado: KeyboardOptions,
    enmascarado: Boolean = false,
    mostrarFuerte: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 33.dp)) {
        Text(
            text = etiqueta,
            fontSize = 26.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(4.dp, colorBorde, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icono),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = TextoMorado,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(TextoMorado),
                visualTransformation = if (enmascarado) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = teclado,
                modifier = Modifier.weight(1f),
                decorationBox = { campoInterno ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (valor.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = TextoMorado.copy(alpha = 0.7f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        campoInterno()
                    }
                }
            )
            if (mostrarFuerte) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(11.dp).height(2.dp).background(BordeRojo, CircleShape))
                    Spacer(Modifier.width(3.dp))
                    Box(modifier = Modifier.width(11.dp).height(2.dp).background(BotonNaranja, CircleShape))
                    Spacer(Modifier.width(3.dp))
                    Box(modifier = Modifier.width(11.dp).height(2.dp).background(TextoVerdeFuerte, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Fuerte",
                        color = TextoVerdeFuerte,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// VISTA 3: NO SE PUDO REGISTRO (Coordenadas exactas del HTML/CSS)
@Composable
fun PantallaErrorServidor(
    onIntentarNuevo: () -> Unit,
    onVolverRegistro: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Logo Pizza (left: 306px, top: 45px, size: 122x67)
        Image(
            painter = painterResource(R.drawable.img_pizza),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = 0.dp)
                .width(122.dp)
                .height(67.dp)
        )

        // Título "¡ups! No se pudo crear la cuenta" (top: 112px, font-size: 40px, width: 313px)
        Text(
            text = "¡ups! No se pudo crear la cuenta",
            color = Color.White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = (-1).sp,
            lineHeight = 42.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 100.dp)
                .width(313.dp)
        )

        // Círculo Rojo (top: 279px, size: 250x250)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 260.dp)
                .size(230.dp)
                .background(Color(0xFFE53935), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                color = Color.White,
                fontSize = 110.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Mensaje de error (top: 544px, font-size: 20px, width: 337px)
        Text(
            text = "Ocurrió un error al crear tu cuenta.\nVerifica tus datos e inténtalo de nuevo.",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 515.dp)
                .width(337.dp)
        )

        // Botón "Intentar de nuevo" (top: 696px, size: 250x59)
        Button(
            onClick = onIntentarNuevo,
            colors = ButtonDefaults.buttonColors(containerColor = BotonNaranja),
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 650.dp)
                .width(250.dp)
                .height(59.dp)
        ) {
            Text(
                text = "Intentar de nuevo",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Enlace "Volver a registro" (top: 774px)
        Text(
            text = "Volver a registro",
            color = Color(0xFFB0AAAA),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 725.dp)
                .clickable { onVolverRegistro() }
        )

        // Pie de página (top: 873px)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            PieUniversidad()
        }
    }
}

// VISTA 4: CUENTA CREADA CON ÉXITO (Utiliza img_check_verde)
@Composable
fun PantallaExito(onIrALogin: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img_pizza),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 11.dp, y = (-4).dp)
                .width(122.dp)
                .height(67.dp)
        )

        // Título (top: 108px, width: 358px, font-size: 40px)
        Text(
            text = "¡Cuenta creada con éxito!",
            color = Color.White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = (-1).sp,
            lineHeight = 42.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 100.dp)
                .width(358.dp)
        )

        // Imagen img_check_verde (top: 255px, size: 300x300)
        Image(
            painter = painterResource(R.drawable.img_check_verde),
            contentDescription = "Cuenta Creada",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 230.dp)
                .size(270.dp)
        )

        // Texto descriptivo (top: 569px, width: 331px, font-size: 21px)
        Text(
            text = "Ya puedes iniciar sesión con tu usuario y contraseña",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 530.dp)
                .width(331.dp)
        )

        // Botón "Ir a iniciar sesión" (top: 780px, size: 304x59)
        Button(
            onClick = onIrALogin,
            colors = ButtonDefaults.buttonColors(containerColor = BotonNaranja),
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 650.dp)
                .width(304.dp)
                .height(59.dp)
        ) {
            Text(
                text = "Ir a iniciar sesión",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Pie de página (top: 874px)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            PieUniversidad()
        }
    }
}

// PREVIEWS
@Preview(showBackground = true, widthDp = 412, heightDp = 917, name = "1. Sign up")
@Composable
private fun PreviewSignUp() {
    BreakAppTheme {
        RegistroScreen(onIrALogin = {})
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917, name = "2. Error Contraseñas")
@Composable
private fun PreviewErrorContrasenas() {
    BreakAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoCSS)
        ) {
            FormularioRegistro(
                state = RegistroUIState(
                    correo = "nombre@gmail.com",
                    usuario = "@nombre_usuario",
                    contrasena = "123456",
                    confirmarContrasena = "654321",
                    error = ErrorRegistro.ContrasenasNoCoinciden
                ),
                viewModel = viewModel()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917, name = "3. Error Servidor")
@Composable
private fun PreviewErrorServidor() {
    BreakAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoCSS)
        ) {
            PantallaErrorServidor(onIntentarNuevo = {})
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917, name = "4. Cuenta Creada")
@Composable
private fun PreviewExito() {
    BreakAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoCSS)
        ) {
            PantallaExito(onIrALogin = {})
        }
    }
}