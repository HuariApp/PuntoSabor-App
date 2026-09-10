package com.example.huariquehub_mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Esquema oscuro — dusk (marrón cálido profundo)
private val DarkColorScheme = darkColorScheme(
    primary              = OrangeDark,
    onPrimary            = SurfaceDark,
    primaryContainer     = Color(0xFF5C2C10),  // brasa oscuro para contenedores
    onPrimaryContainer   = OrangeLight,
    secondary            = BrownMedium,
    onSecondary          = SurfaceColor,
    secondaryContainer   = Color(0xFF3D2416),
    onSecondaryContainer = BrownLight,
    background           = SurfaceDark,
    surface              = Color(0xFF342819),
    onBackground         = Color(0xFFF5EDE4),
    onSurface            = Color(0xFFF5EDE4),
    surfaceVariant       = Color(0xFF3D2C1E),
    onSurfaceVariant     = TextTertiary,
    outline              = Color(0xFF5C4A3A),
    error                = ErrorRed
)

// Esquema claro — warm tone, acento brasa
private val LightColorScheme = lightColorScheme(
    primary              = OrangePrimary,
    onPrimary            = SurfaceColor,
    primaryContainer     = OrangeLight,
    onPrimaryContainer   = BrownDark,
    secondary            = BrownMedium,
    onSecondary          = SurfaceColor,
    secondaryContainer   = BackgroundSoft,
    onSecondaryContainer = BrownDark,
    tertiary             = BrownDark,
    onTertiary           = SurfaceColor,
    tertiaryContainer    = WarmCream,
    onTertiaryContainer  = BrownDark,
    background           = WarmWhite,
    surface              = SurfaceColor,
    surfaceVariant       = BackgroundSoft,
    onBackground         = TextPrimary,
    onSurface            = TextPrimary,
    onSurfaceVariant     = TextSecondary,
    outline              = DividerWarm,
    outlineVariant       = Color(0xFFEEE6DD),
    error                = ErrorRed
)

@Composable
fun HuariqueHubMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
