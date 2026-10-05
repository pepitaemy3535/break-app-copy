package com.example.breakapp.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Crema
import com.example.breakapp.ui.theme.DoradoResplandor
import com.example.breakapp.ui.theme.Fondo
import kotlinx.coroutines.delay

private const val DURACION_SPLASH_MS = 5_000L

@Composable
fun SplashScreen(
    onTiempoCumplido: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alTerminar by rememberUpdatedState(onTiempoCumplido)
    LaunchedEffect(Unit) {
        delay(DURACION_SPLASH_MS)
        alTerminar()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1.8f))
        LogoConResplandor()
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.marca_nombre),
            style = BreakText.Marca,
            color = Color.White
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.marca_eslogan),
            style = BreakText.Eslogan,
            color = Crema,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        Cargador()
        Spacer(Modifier.weight(1f))
        PieUniversidad()
    }
}

@Composable
private fun LogoConResplandor() {
    Box(
        modifier = Modifier.size(304.dp),
        contentAlignment = Alignment.Center
    ) {
        // Resplandor dorado. En Android 11 o inferior el desenfoque no se aplica.
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0.7f)
                .blur(20.dp, BlurredEdgeTreatment.Unbounded)
                .background(DoradoResplandor, RoundedCornerShape(79.dp))
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .background(Color.White, RoundedCornerShape(79.dp))
        )
        Image(
            painter = painterResource(R.drawable.img_logo),
            contentDescription = stringResource(R.string.logo_descripcion),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(245.dp)
        )
    }
}

@Composable
private fun Cargador() {
    val transicion = rememberInfiniteTransition(label = "giroCargador")
    val angulo by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2000, easing = LinearEasing)),
        label = "anguloCargador"
    )
    Image(
        painter = painterResource(R.drawable.img_loader),
        contentDescription = stringResource(R.string.cargando_descripcion),
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer { rotationZ = angulo }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun SplashScreenPreview() {
    BreakAppTheme { SplashScreen(onTiempoCumplido = {}) }
}
