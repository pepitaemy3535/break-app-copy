package com.example.breakapp.main.restaurant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.main.components.BreakHeader
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts
import com.example.breakapp.main.data.menuRepository
import com.example.breakapp.model.Tienda
import com.example.breakapp.ui.home.HomeViewModel

/** Figma: "Cafeterías y restaurantes" (se abre con "Ver todos" del Inicio). */
@Composable
fun RestaurantsScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onTiendaClick: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Si Firestore no tiene tiendas registradas o falla la conexión, usamos el catálogo local
    val tiendasAMostrar = remember(state.tiendas) {
        if (state.tiendas.isNotEmpty()) {
            state.tiendas
        } else {
            menuRepository.restaurants().map {
                Tienda(
                    id = it.id,
                    nombre = it.name,
                    calificacion = it.rating,
                    horario = it.timeRange
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        BreakHeader()

        Spacer(Modifier.height(19.dp))

        Text(
            text = "Cafeterías y Restaurantes",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(Modifier.height(22.dp))

        if (state.isLoading && tiendasAMostrar.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(19.dp)
            ) {
                tiendasAMostrar.forEach { tienda ->
                    // Asociación exacta con los nombres de tus imágenes en drawable
                    val imagenResId = when {
                        tienda.nombre.contains("Catering", ignoreCase = true) || tienda.id.contains("cater", ignoreCase = true) -> R.drawable.main_img_rest_catering_gourmet
                        tienda.nombre.contains("World", ignoreCase = true) || tienda.id.contains("world", ignoreCase = true) -> R.drawable.main_img_rest_world_gourmet
                        tienda.nombre.contains("People", ignoreCase = true) || tienda.nombre.contains("pizza", ignoreCase = true) || tienda.id.contains("pizza", ignoreCase = true) || tienda.id.contains("people", ignoreCase = true) -> R.drawable.main_img_rest_peoples_pizza
                        else -> R.drawable.main_img_rest_fruit_coffee
                    }

                    // Resolución segura del ID del restaurante para no caer erróneamente en Catering
                    val targetId = when {
                        tienda.id == "peoples_pizza" || tienda.nombre.contains("People", ignoreCase = true) || tienda.nombre.contains("pizza", ignoreCase = true) || tienda.id.contains("pizza", ignoreCase = true) || tienda.id.contains("people", ignoreCase = true) -> "peoples_pizza"
                        tienda.id == "world_gourmet" || tienda.nombre.contains("World", ignoreCase = true) || tienda.id.contains("world", ignoreCase = true) -> "world_gourmet"
                        tienda.id == "fruit_coffee" || tienda.nombre.contains("Fruit", ignoreCase = true) || tienda.nombre.contains("coffee", ignoreCase = true) || tienda.id.contains("fruit", ignoreCase = true) -> "fruit_coffee"
                        tienda.id == "catering_gourmet" || tienda.nombre.contains("Catering", ignoreCase = true) || tienda.nombre.contains("Catherine", ignoreCase = true) || tienda.id.contains("cater", ignoreCase = true) -> "catering_gourmet"
                        else -> tienda.id.ifBlank { "catering_gourmet" }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTiendaClick(targetId) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White // Tarjeta completamente blanca para fundir los logos
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // --- ESTA ES LA PARTE CORREGIDA ---
                            Image(
                                painter = painterResource(id = imagenResId),
                                contentDescription = tienda.nombre,
                                modifier = Modifier
                                    .size(90.dp), // Mantenemos el tamaño, pero sin clip forzoso
                                contentScale = ContentScale.Fit // ¡IMPORTANTE! Ajusta la imagen entera sin recortar
                            )
                            // ----------------------------------

                            Spacer(modifier = Modifier.width(16.dp))

                            // Información a la derecha
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = tienda.nombre,
                                    fontFamily = BreakFonts.Inter,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⭐", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${tienda.calificacion}",
                                        fontFamily = BreakFonts.Inter,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = tienda.horario,
                                        fontFamily = BreakFonts.Inter,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}