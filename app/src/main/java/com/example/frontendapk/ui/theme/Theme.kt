package com.example.frontendapk.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color




private val DarkColorScheme = darkColorScheme(
    primary = TealDark,
    onPrimary = Color.White,
    secondary = GreenGray,
    onSecondary = Color.White,
    tertiary = ButtonYellow,
    background = GrayDarker,
    onBackground = Color.White,
    surface = GrayDark,
    onSurface = Color.White,
    error = ButtonRed
)

private val LightColorScheme = lightColorScheme(
    primary = TealMedium,
    onPrimary = Color.White,
    secondary = TealLight,
    onSecondary = Color.White,
    tertiary = ButtonYellow,
    background = Color.White,
    onBackground = GrayDark,
    surface = Color.White,
    onSurface = GrayDark,
    error = ButtonRed

)

@Composable
fun FrontendAPKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    adminMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        adminMode -> LightColorScheme // Forzar tema claro en admin
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // o cambiar a LightColorScheme si prefieres
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}