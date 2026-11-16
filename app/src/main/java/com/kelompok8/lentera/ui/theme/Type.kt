package com.kelompok8.lentera.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R

val MuseoModerno = FontFamily(
    Font(R.font.museomodernothin, FontWeight.Thin),
    Font(R.font.museomodernoextralight, FontWeight.ExtraLight),
    Font(R.font.museomodernolight, FontWeight.Light),
    Font(R.font.museomodernoregular, FontWeight.Normal),
    Font(R.font.museomodernomedium, FontWeight.Medium),
    Font(R.font.museomodernosemibold, FontWeight.SemiBold),
    Font(R.font.museomodernobold, FontWeight.Bold),
    Font(R.font.museomodernoextrabold, FontWeight.ExtraBold),
    Font(R.font.museomodernoblack, FontWeight.Black),
)

// Alias NuesoModerno agar tetap dapat digunakan dengan nama ini
val NuesoModerno = MuseoModerno

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MuseoModerno,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)