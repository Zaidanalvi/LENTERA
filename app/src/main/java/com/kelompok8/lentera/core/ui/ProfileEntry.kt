package com.kelompok8.lentera.core.ui

import androidx.compose.runtime.staticCompositionLocalOf

// Aksi "buka profil" yang dipasang sekali di LenteraApp, lalu dipakai oleh
// avatar di Header tanpa harus mengubah pemanggil Header di tiap halaman.
val LocalOpenProfile = staticCompositionLocalOf<(() -> Unit)?> { null }
