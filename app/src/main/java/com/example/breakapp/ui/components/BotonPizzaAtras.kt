package com.example.breakapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.breakapp.R

/** Las pizzas cruzadas de la esquina superior derecha: sirven para volver atrás. */
@Composable
fun BotonPizzaAtras(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.img_pizza),
        contentDescription = stringResource(R.string.volver_descripcion),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .offset(x = 16.dp)
            .width(122.dp)
            .height(67.dp)
            .clickable(role = Role.Button, onClick = onClick)
    )
}
