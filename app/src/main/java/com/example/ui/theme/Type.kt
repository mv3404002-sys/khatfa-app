package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage

// IBM Plex Sans Arabic - Modern, technical, high-legibility Arabic & Latin typeface
val IbmPlexSansArabicFontFamily = FontFamily(
  Font(R.font.ibm_plex_sans_arabic, FontWeight.Normal),
  Font(R.font.ibm_plex_sans_arabic, FontWeight.Medium),
  Font(R.font.ibm_plex_sans_arabic, FontWeight.SemiBold),
  Font(R.font.ibm_plex_sans_arabic, FontWeight.Bold)
)

// Compatibility aliases
val CairoFontFamily = IbmPlexSansArabicFontFamily
val OutfitFontFamily = IbmPlexSansArabicFontFamily
val CinzelFontFamily = IbmPlexSansArabicFontFamily

fun getAppTypography(language: AppLanguage, fontScale: Float = 1.0f): Typography {
  val primaryFont = IbmPlexSansArabicFontFamily

  return Typography(
    displayLarge = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Bold,
      fontSize = (30 * fontScale).sp,
      lineHeight = (38 * fontScale).sp
    ),
    displayMedium = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Bold,
      fontSize = (24 * fontScale).sp,
      lineHeight = (32 * fontScale).sp
    ),
    displaySmall = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Bold,
      fontSize = (20 * fontScale).sp,
      lineHeight = (28 * fontScale).sp
    ),
    headlineLarge = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Bold,
      fontSize = (22 * fontScale).sp,
      lineHeight = (30 * fontScale).sp
    ),
    headlineMedium = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (19 * fontScale).sp,
      lineHeight = (26 * fontScale).sp
    ),
    headlineSmall = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (17 * fontScale).sp,
      lineHeight = (24 * fontScale).sp
    ),
    titleLarge = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Bold,
      fontSize = (19 * fontScale).sp,
      lineHeight = (26 * fontScale).sp
    ),
    titleMedium = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (16.5 * fontScale).sp,
      lineHeight = (23 * fontScale).sp
    ),
    titleSmall = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (14.5 * fontScale).sp,
      lineHeight = (21 * fontScale).sp
    ),
    bodyLarge = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Normal,
      fontSize = (15.5 * fontScale).sp,
      lineHeight = (23 * fontScale).sp
    ),
    bodyMedium = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Normal,
      fontSize = (13.5 * fontScale).sp,
      lineHeight = (20 * fontScale).sp
    ),
    bodySmall = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Normal,
      fontSize = (12 * fontScale).sp,
      lineHeight = (17 * fontScale).sp
    ),
    labelLarge = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.SemiBold,
      fontSize = (13.5 * fontScale).sp,
      lineHeight = (18 * fontScale).sp
    ),
    labelMedium = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Medium,
      fontSize = (12 * fontScale).sp,
      lineHeight = (16 * fontScale).sp
    ),
    labelSmall = TextStyle(
      fontFamily = primaryFont,
      fontWeight = FontWeight.Normal,
      fontSize = (11 * fontScale).sp,
      lineHeight = (15 * fontScale).sp
    )
  )
}
