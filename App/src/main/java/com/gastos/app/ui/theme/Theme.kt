package com.gastos.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    onPrimary = BackgroundDark,
    primaryContainer = DeepTeal,
    onPrimaryContainer = MintGreen,
    
    secondary = SkyBlue,
    onSecondary = BackgroundDark,
    secondaryContainer = OceanBlue,
    onSecondaryContainer = SkyBlue,
    
    tertiary = SoftCoral,
    onTertiary = BackgroundDark,
    tertiaryContainer = CoralOrange,
    onTertiaryContainer = SoftCoral,
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextSecondaryDark,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    primaryContainer = LightMint,
    onPrimaryContainer = DeepTeal,
    
    secondary = OceanBlue,
    onSecondary = Color.White,
    secondaryContainer = SoftBlue,
    onSecondaryContainer = DeepTeal,
    
    tertiary = CoralOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E1),
    onTertiaryContainer = Color(0xFF7F1D1D),
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
)

@Composable
fun GastosAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color deshabilitado para usar nuestra paleta personalizada
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}