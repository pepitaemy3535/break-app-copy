package com.example.breakapp.ui.perfil.subpantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.components.PerfilMenuItem
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.Fondo

@Composable
fun AyudaSoporteScreen(
    onVolver: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PerfilTopBar(
            titulo = "Ayuda y soporte",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PerfilMenuItem(
                titulo = "Preguntas frecuentes",
                icono = Icons.Default.Help,
                onClick = { /* FAQ */ }
            )
            PerfilMenuItem(
                titulo = "Chat con soporte",
                icono = Icons.Default.MailOutline,
                onClick = { /* Chat */ }
            )
            PerfilMenuItem(
                titulo = "Reportar un problema",
                icono = Icons.Default.ReportProblem,
                onClick = { /* Reportar */ }
            )
            PerfilMenuItem(
                titulo = "Términos y condiciones",
                icono = Icons.Default.Description,
                onClick = { /* Terminos */ }
            )
            PerfilMenuItem(
                titulo = "Calificar la app",
                icono = Icons.Default.Star,
                onClick = { /* Play Store */ }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Sección CONTÁCTANOS
            Text(
                text = "CONTÁCTANOS",
                color = Ambar,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF381B7A))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Ambar,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ayuda@break.com",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = Ambar,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "+1 (800) 123-4567",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun AyudaSoporteScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        AyudaSoporteScreen(
            onVolver = {}
        )
    }
}
