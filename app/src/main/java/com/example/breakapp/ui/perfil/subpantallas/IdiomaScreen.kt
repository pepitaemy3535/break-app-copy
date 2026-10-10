package com.example.breakapp.ui.perfil.subpantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.PerfilEvent
import com.example.breakapp.ui.perfil.PerfilUiState
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.Fondo

@Composable
fun IdiomaScreen(
    state: PerfilUiState,
    onEvent: (PerfilEvent) -> Unit,
    onVolver: () -> Unit
) {
    val idiomas = listOf("Español", "English", "Português", "Français")
    val forma = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PerfilTopBar(
            titulo = "Idioma",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            idiomas.forEach { idioma ->
                val seleccionado = state.idiomaSeleccionado == idioma
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(forma)
                        .background(Color(0xFF381B7A))
                        .clickable { onEvent(PerfilEvent.OnIdiomaSelected(idioma)) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = idioma,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    RadioButton(
                        selected = seleccionado,
                        onClick = { onEvent(PerfilEvent.OnIdiomaSelected(idioma)) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Ambar,
                            unselectedColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun IdiomaScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        IdiomaScreen(
            state = PerfilUiState(),
            onEvent = {},
            onVolver = {}
        )
    }
}
