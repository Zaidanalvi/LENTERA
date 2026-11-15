package com.kelompok8.lentera.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.core.ui.LenteraLogo
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.ui.theme.AboutCard
import com.kelompok8.lentera.ui.theme.TextLight

private val team = listOf(
    "Product Manager" to "Zaidan Alvi Zulfan Pramudya",
    "Peta, Chapter & Leaderboard" to "Ahmad Hilalul Fadli",
    "Penilaian & Pencapaian" to "T. M. Panji Fachroni",
    "Permainan & Validasi" to "Daud Fathin Averroes",
    "Glosarium Budaya" to "Arinda Magisya Putri Noflasya"
)

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LenteraLogo()
        Spacer(Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AboutCard)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "TENTANG KAMI",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "LENTERA adalah aplikasi untuk mengenal kekayaan budaya Indonesia dari Pulau Jawa, " +
                    "Sumatra, dan Kalimantan. Lewat kuis, permainan blok kata, susun huruf, dan glosarium " +
                    "budaya, LENTERA mengajak pelajar melestarikan budaya Nusantara dengan cara yang seru.",
                color = TextLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Justify
            )
            HorizontalDivider(color = TextLight.copy(alpha = 0.6f))
            Column {
                InfoText("LENTERA")
                InfoText("Version : 1.0")
                InfoText("Released : 2026")
            }
            HorizontalDivider(color = TextLight.copy(alpha = 0.6f))
            Text(
                "TIM KITA",
                color = TextLight,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                team.forEach { (role, name) -> InfoText("$role : $name") }
            }
        }

        Spacer(Modifier.height(32.dp))
        PinkButton(text = "Kembali", onClick = onBack)
    }
}

@Composable
private fun InfoText(text: String) {
    Text(text, color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
}
