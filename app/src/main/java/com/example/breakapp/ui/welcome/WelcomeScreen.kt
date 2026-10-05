package com.example.breakapp.ui.welcome

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.BordeBeneficioAvisos
import com.example.breakapp.ui.theme.BordeBeneficioConsulta
import com.example.breakapp.ui.theme.BordeBeneficioPaga
import com.example.breakapp.ui.theme.BordeBeneficioPide
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo

@Composable
fun WelcomeScreen(
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.bienvenida_titulo),
            style = BreakText.TituloPantalla,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 27.dp)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.bienvenida_subtitulo),
            style = BreakText.Cuerpo,
            color = Color.White,
            modifier = Modifier
                .padding(start = 36.dp)
                .widthIn(max = 298.dp)
        )
        Spacer(Modifier.height(27.dp))

        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Row(
                modifier = Modifier.heightIn(min = 246.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Beneficio(R.drawable.img_funcion_consulta, R.string.beneficio_consulta, BordeBeneficioConsulta)
                Beneficio(R.drawable.img_funcion_pide, R.string.beneficio_pide, BordeBeneficioPide)
            }
            Spacer(Modifier.height(52.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Beneficio(R.drawable.img_funcion_avisos, R.string.beneficio_avisos, BordeBeneficioAvisos)
                Beneficio(R.drawable.img_funcion_paga, R.string.beneficio_paga, BordeBeneficioPaga)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 7.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono de sol del diseño: por ahora sin acción (pendiente de definir su función).
            Image(
                painter = painterResource(R.drawable.img_sol),
                contentDescription = stringResource(R.string.bienvenida_sol_descripcion),
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(25.dp))
            )
            BotonPrimario(
                texto = stringResource(R.string.bienvenida_siguiente),
                onClick = onSiguiente,
                modifier = Modifier.width(204.dp)
            )
        }
        Spacer(Modifier.height(11.dp))
        PieUniversidad()
    }
}

@Composable
private fun Beneficio(
    @DrawableRes imagen: Int,
    @StringRes texto: Int,
    colorBorde: Color
) {
    val forma = RoundedCornerShape(50.dp)
    Column(
        modifier = Modifier.width(165.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imagen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(165.dp)
                .clip(forma)
                .border(5.dp, colorBorde, forma)
        )
        Spacer(Modifier.height(23.dp))
        Text(
            text = stringResource(texto),
            style = BreakText.Cuerpo,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun WelcomeScreenPreview() {
    BreakAppTheme { WelcomeScreen(onSiguiente = {}) }
}
