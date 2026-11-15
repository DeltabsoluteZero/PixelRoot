package com.pixelroot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Pure Monochrome Material 3 Dark Palette (OLED-friendly grayscale tonal hierarchy)
private val MonochromeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF101010),
    primaryContainer = Color(0xFF2A2A2A),
    onPrimaryContainer = Color(0xFFF4F4F4),
    secondary = Color(0xFFC6C6C6),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF222222),
    onSecondaryContainer = Color(0xFFE4E4E4),
    tertiary = Color(0xFFA8A8A8),
    onTertiary = Color(0xFF161616),
    tertiaryContainer = Color(0xFF262626),
    onTertiaryContainer = Color(0xFFDCDCDC),
    background = Color(0xFF090909),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF090909),
    onSurface = Color(0xFFF2F2F2),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFB0B0B0),
    surfaceContainerLowest = Color(0xFF050505),
    surfaceContainerLow = Color(0xFF111111),
    surfaceContainer = Color(0xFF171717),
    surfaceContainerHigh = Color(0xFF202020),
    surfaceContainerHighest = Color(0xFF2B2B2B),
    outline = Color(0xFF525252),
    outlineVariant = Color(0xFF292929),
    inverseSurface = Color(0xFFEDEDED),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF1A1A1A),
    scrim = Color(0xCC000000)
)

// Pure Monochrome Material 3 Light Palette (E-Ink / Paper grayscale tonal hierarchy)
private val MonochromeLightColorScheme = lightColorScheme(
    primary = Color(0xFF121212),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E2E2),
    onPrimaryContainer = Color(0xFF121212),
    secondary = Color(0xFF474747),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEAEAEA),
    onSecondaryContainer = Color(0xFF1B1B1B),
    tertiary = Color(0xFF5E5E5E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE6E6E6),
    onTertiaryContainer = Color(0xFF1E1E1E),
    background = Color(0xFFF8F8F8),
    onBackground = Color(0xFF111111),
    surface = Color(0xFFF8F8F8),
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFE6E6E6),
    onSurfaceVariant = Color(0xFF4A4A4A),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F2F2),
    surfaceContainer = Color(0xFFECECEC),
    surfaceContainerHigh = Color(0xFFE4E4E4),
    surfaceContainerHighest = Color(0xFFDCDCDC),
    outline = Color(0xFF787878),
    outlineVariant = Color(0xFFD0D0D0),
    inverseSurface = Color(0xFF1C1C1C),
    inverseOnSurface = Color(0xFFF4F4F4),
    inversePrimary = Color(0xFFFFFFFF),
    scrim = Color(0x99000000)
)

val PixelRootTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 1.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp
    )
)

@Composable
fun PixelRootTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MonochromeDarkColorScheme else MonochromeLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PixelRootTypography,
        content = content
    )
}
