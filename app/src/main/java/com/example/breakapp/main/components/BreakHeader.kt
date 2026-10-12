package com.example.breakapp.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

/** Título "¿Qué quieres pedir hoy?" + logo de Break. Se repite en todas las pantallas. */
@Composable
fun BreakHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(142.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 14.dp, top = 22.dp)
                .width(201.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Qué quieres",
                fontFamily = BreakFonts.ImFell,
                fontSize = 36.sp,
                lineHeight = 38.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "pedir hoy?",
                fontFamily = BreakFonts.ImFell,
                fontSize = 36.sp,
                lineHeight = 38.sp,
                color = BreakColors.Amber,
                textAlign = TextAlign.Center
            )
        }
        Image(
            painter = painterResource(R.drawable.main_img_logo),
            contentDescription = "Break",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp)
                .size(79.dp)
                .clip(CircleShape)
        )
    }
}
