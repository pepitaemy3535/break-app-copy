package com.example.breakapp.ui.ai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@Composable
fun AiRecommendationsScreen(
    viewModel: AiViewModel,
    onBack: () -> Unit,
    onExploreRestaurants: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var inputText by remember { mutableStateOf("") }
    var lastPrompt by remember { mutableStateOf("") }

    val chips = listOf(
        "🍱 ¿Qué almuerzo hoy?",
        "⚡ Algo rápido para receso",
        "☕ Café y snack",
        "🍰 Antojo dulce",
        "💰 Menos de $10.000",
        "🥗 Opción ligera o saludable"
    )

    fun sendPrompt(prompt: String) {
        val clean = prompt.trim()
        if (clean.isBlank()) return
        lastPrompt = clean
        inputText = ""
        focusManager.clearFocus()
        viewModel.enviarMensaje(clean)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Break IA",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BreakColors.Amber.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BreakColors.Amber,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Agente",
                                fontFamily = BreakFonts.Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BreakColors.Amber
                            )
                        }
                    }
                }
                Text(
                    text = "Recomendaciones a tu medida",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Image(
                painter = painterResource(R.drawable.main_img_logo),
                contentDescription = "Break Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        // --- CHIPS HORIZONTALES CON SUGERENCIAS RÁPIDAS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach { chipText ->
                Surface(
                    color = Color(0xFF381E72),
                    shape = RoundedCornerShape(20.dp),
                    border = null,
                    modifier = Modifier.clickable {
                        sendPrompt(chipText)
                    }
                ) {
                    Text(
                        text = chipText,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // --- CONTENIDO PRINCIPAL (SCROLLABLE) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                viewModel.isLoading -> {
                    LoadingView()
                }

                viewModel.respuestaIa.isNotBlank() -> {
                    val isError = viewModel.respuestaIa.startsWith("Error de conexión", ignoreCase = true)
                    if (isError) {
                        ErrorCard(
                            errorMessage = viewModel.respuestaIa,
                            onRetry = {
                                if (lastPrompt.isNotBlank()) {
                                    sendPrompt(lastPrompt)
                                } else {
                                    sendPrompt("¿Qué me recomiendas pedir hoy?")
                                }
                            }
                        )
                    } else {
                        ResponseCard(
                            prompt = lastPrompt,
                            response = viewModel.respuestaIa,
                            onAskAnother = {
                                inputText = ""
                            },
                            onExplore = onExploreRestaurants
                        )
                    }
                }

                else -> {
                    InitialWelcomeCard(
                        onQuickStart = {
                            sendPrompt("Recomiéndame opciones deliciosas para pedir hoy en el campus")
                        }
                    )
                }
            }
        }

        // --- CAMPO INFERIOR PARA ESCRIBIR A LA IA ---
        Surface(
            color = Color(0xFF1E103E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Pregúntale a la IA qué comer...",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2C1462),
                        unfocusedContainerColor = Color(0xFF2C1462),
                        focusedBorderColor = BreakColors.Amber,
                        unfocusedBorderColor = Color(0xFF4C2A8A)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { sendPrompt(inputText) })
                )

                Spacer(Modifier.width(10.dp))

                IconButton(
                    onClick = { sendPrompt(inputText) },
                    enabled = inputText.isNotBlank() && !viewModel.isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !viewModel.isLoading) BreakColors.Amber else Color(0xFF4C2A8A))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (inputText.isNotBlank() && !viewModel.isLoading) Color(0xFF26184C) else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = BreakColors.Amber,
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Tu agente Break está analizando el menú...",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Buscando las mejores opciones según lo que buscas",
            fontFamily = BreakFonts.Inter,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InitialWelcomeCard(onQuickStart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF381E72))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BreakColors.Amber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BreakColors.Amber,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "¡Hola! ¿No sabes qué pedir?",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Puedo sugerirte platillos, bebidas y postres de los módulos según tu presupuesto, la hora o tus antojos.",
                fontFamily = BreakFonts.Inter,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onQuickStart,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BreakColors.Amber,
                    contentColor = Color(0xFF26184C)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF26184C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Dame sugerencias para hoy",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF26184C)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponseCard(
    prompt: String,
    response: String,
    onAskAnother: () -> Unit,
    onExplore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        if (prompt.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                    .background(Color(0xFF4C2A8A))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = prompt,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF381E72)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BreakColors.Amber.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Recomendación de Break IA",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BreakColors.Amber
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = response,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onExplore) {
                        Text(
                            text = "Explorar módulos",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            color = BreakColors.Amber,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1828)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF8A80),
                modifier = Modifier.size(36.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "No se pudo conectar con la IA",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Recuerda que el servidor backend de Python debe estar corriendo en http://127.0.0.1:8000/ con el endpoint /api/chat.\n\nDetalle: $errorMessage",
                fontFamily = BreakFonts.Inter,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber, contentColor = Color(0xFF26184C))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Reintentar",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
