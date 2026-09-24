package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage

val CairoFontFamily = FontFamily(
  Font(R.font.cairo_regular, FontWeight.Light),
  Font(R.font.cairo_regular, FontWeight.Normal),
  Font(R.font.cairo_semibold, FontWeight.Medium),
  Font(R.font.cairo_semibold, FontWeight.SemiBold),
  Font(R.font.cairo_bold, FontWeight.Bold),
  Font(R.font.cairo_bold, FontWeight.ExtraBold)
)

val CinzelFontFamily = FontFamily(
  Font(R.font.cinzel, FontWeight.Normal),
  Font(R.font.cinzel, FontWeight.Bold)
)

val OutfitFontFamily = FontFamily(
  Font(R.font.outfit, FontWeight.Normal),
  Font(R.font.outfit, FontWeight.Medium),
  Font(R.font.outfit, FontWeight.Bold)
)

fun getAppTypography(language: AppLanguage, fontScale: Float = 1.0f): Typography {
  val isArabic = language == AppLanguage.ARABIC
  val titleFont = if (isArabic) CairoFontFamily else CinzelFontFamily
  val bodyFont = if (isArabic) CairoFontFamily else OutfitFontFamily

  return Typography(
    displayLarge = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.ExtraBold,
      fontSize = (30 * fontScale).sp,
      lineHeight = (40 * fontScale).sp
    ),
    displayMedium = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.Bold,
      fontSize = (24 * fontScale).sp,
      lineHeight = (32 * fontScale).sp
    ),
    displaySmall = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.Bold,
      fontSize = (20 * fontScale).sp,
      lineHeight = (28 * fontScale).sp
    ),
    headlineLarge = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.Bold,
      fontSize = (24 * fontScale).sp,
      lineHeight = (32 * fontScale).sp
    ),
    headlineMedium = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (20 * fontScale).sp,
      lineHeight = (28 * fontScale).sp
    ),
    headlineSmall = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (18 * fontScale).sp,
      lineHeight = (26 * fontScale).sp
    ),
    titleLarge = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.Bold,
      fontSize = (20 * fontScale).sp,
      lineHeight = (28 * fontScale).sp
    ),
    titleMedium = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (17.5 * fontScale).sp,
      lineHeight = (24 * fontScale).sp
    ),
    titleSmall = TextStyle(
      fontFamily = titleFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (14.5 * fontScale).sp,
      lineHeight = (21 * fontScale).sp
    ),
    bodyLarge = TextStyle(
      fontFamily = bodyFont,
      fontWeight = FontWeight.Normal,
      fontSize = (15.5 * fontScale).sp,
      lineHeight = (23 * fontScale).sp
    ),
    bodyMedium = TextStyle(
      fontFamily = bodyFont,
      fontWeight = FontWeight.Normal,
      fontSize = (13.5 * fontScale).sp,
      lineHeight = (20 * fontScale).sp
    ),
    bodySmall = TextStyle(
      fontFamily = bodyFont,
      fontWeight = FontWeight.Normal,
      fontSize = (12 * fontScale).sp,
      lineHeight = (17 * fontScale).sp
    ),
    labelLarge = TextStyle(
      fontFamily = bodyFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (13.5 * fontScale).sp,
      lineHeight = (18 * fontScale).sp
    ),
    labelMedium = TextStyle(
      fontFamily = bodyFont,
      fontWeight = FontWeight.Medium,
      fontSize = (12 * fontScale).sp,
      lineHeight = (16 * fontScale).sp
    ),
    labelSmall = TextStyle(
      fontFamily = bodyFont,
      fontWeight = FontWeight.Normal,
      fontSize = (11 * fontScale).sp,
      lineHeight = (15 * fontScale).sp
    )
  )
}
