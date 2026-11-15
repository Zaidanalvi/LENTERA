package com.kelompok8.lentera.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.NavBarBlue

data class BottomItem(val route: String, val label: String, val icon: String)

// Bottom bar sesuai Figma: tanpa label, item aktif ditandai titik hijau di bawah ikon.
// Ikon masih emoji, nanti diganti ikon dari Figma.
@Composable
fun LenteraBottomBar(
    items: List<BottomItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(NavBarBlue)
            .navigationBarsPadding()
            .padding(vertical = 12.dp)
    ) {
        items.forEach { item ->
            val selected = item.route == currentRoute
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onItemClick(item.route) }
                    .padding(vertical = 6.dp)
            ) {
                Text(item.icon, fontSize = if (selected) 28.sp else 24.sp)
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (selected) LenteraLime else Color.Transparent)
                )
            }
        }
    }
}
