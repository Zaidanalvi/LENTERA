package com.kelompok8.lentera.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kelompok8.lentera.core.ui.LenteraLogo
import com.kelompok8.lentera.core.ui.LinkText
import com.kelompok8.lentera.core.ui.PinkButton

// Halaman "Pengenalan" di Figma, muncul kalau belum ada sesi login
@Composable
fun WelcomeScreen(onStart: () -> Unit, onAbout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp, vertical = 44.dp)
    ) {
        Spacer(Modifier.weight(0.22f))
        LenteraLogo()
        Spacer(Modifier.weight(0.78f))
        PinkButton(text = "Mulai Bermain", onClick = onStart, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        LinkText(text = "BACA TENTANG KAMI", onClick = onAbout)
    }
}
