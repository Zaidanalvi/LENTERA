package com.kelompok8.lentera.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LenteraLogo()
        Spacer(modifier = Modifier.height(500.dp))
        PinkButton(text = "Mulai Bermain", onClick = onStart, modifier = Modifier.width(300.dp))
        Spacer(Modifier.height(20.dp))
        LinkText(text = "BACA TENTANG KAMI", onClick = onAbout)
    }
}
