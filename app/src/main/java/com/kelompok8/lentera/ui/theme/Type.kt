package com.kelompok8.lentera.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kelompok8.lentera.R

val museoModerno = FontFamily(
    Font(R.font.museomodernothin, FontWeight.Thin),
    Font(R.font.museomodernoextralight, FontWeight.ExtraLight),
    Font(R.font.museomodernolight, FontWeight.Light),
    Font(R.font.museomodernoregular, FontWeight.Normal),
    Font(R.font.museomodernomedium, FontWeight.Medium),
    Font(R.font.museomodernosemibold, FontWeight.SemiBold),
    Font(R.font.museomodernobold, FontWeight.Bold),
    Font(R.font.museomodernoextrabold, FontWeight.ExtraBold),
    Font(R.font.museomodernoblack, FontWeight.Black)
)

val mPlus1p = FontFamily(
    Font(R.font.mplus1pthin, FontWeight.Thin),
    Font(R.font.mplus1plight, FontWeight.ExtraLight),
    Font(R.font.mplus1plight, FontWeight.Light),
    Font(R.font.mplus1pregular, FontWeight.Normal),
    Font(R.font.mplus1pmedium, FontWeight.Medium),
    Font(R.font.mplus1pbold, FontWeight.SemiBold),
    Font(R.font.mplus1pbold, FontWeight.Bold),
    Font(R.font.mplus1pextrabold, FontWeight.ExtraBold),
    Font(R.font.mplus1pblack, FontWeight.Black)
)

// Hilangkan ekstra font padding bawaan M PLUS 1p agar tinggi baris konsisten di semua HP
private val DefaultPlatformTextStyle = PlatformTextStyle(includeFontPadding = false)
private val BaseTypography = Typography()

private fun TextStyle.withAppFont(): TextStyle = copy(
    fontFamily = mPlus1p,
    platformStyle = DefaultPlatformTextStyle
)

val Typography = Typography(
    displayLarge = BaseTypography.displayLarge.withAppFont(),
    displayMedium = BaseTypography.displayMedium.withAppFont(),
    displaySmall = BaseTypography.displaySmall.withAppFont(),
    headlineLarge = BaseTypography.headlineLarge.withAppFont(),
    headlineMedium = BaseTypography.headlineMedium.withAppFont(),
    headlineSmall = BaseTypography.headlineSmall.withAppFont(),
    titleLarge = BaseTypography.titleLarge.withAppFont(),
    titleMedium = BaseTypography.titleMedium.withAppFont(),
    titleSmall = BaseTypography.titleSmall.withAppFont(),
    bodyLarge = BaseTypography.bodyLarge.withAppFont(),
    bodyMedium = BaseTypography.bodyMedium.withAppFont(),
    bodySmall = BaseTypography.bodySmall.withAppFont(),
    labelLarge = BaseTypography.labelLarge.withAppFont(),
    labelMedium = BaseTypography.labelMedium.withAppFont(),
    labelSmall = BaseTypography.labelSmall.withAppFont()
)