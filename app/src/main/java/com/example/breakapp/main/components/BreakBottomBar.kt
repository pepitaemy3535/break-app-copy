package com.example.breakapp.main.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

enum class MainTab(val label: String, @DrawableRes val icon: Int) {
    INICIO("Inicio", R.drawable.main_ic_nav_home),
    BUSCAR("Buscar", R.drawable.main_ic_nav_search),
    CARRITO("Carrito", R.drawable.main_ic_nav_bag),
    PEDIDOS("Pedidos", R.drawable.main_ic_nav_clock),
    PERFIL("Perfil", R.drawable.main_ic_nav_user)
}

@Composable
fun BreakBottomBar(
    selected: MainTab,
    onTabClick: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTab.values().forEach { tab ->
            val isSelected = tab == selected
            val color = if (isSelected) BreakColors.Amber else BreakColors.Gray
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabClick(tab) }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(tab.icon),
                    contentDescription = tab.label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = tab.label,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 10.sp,
                    color = color,
                    maxLines = 1
                )
            }
        }
    }
}
