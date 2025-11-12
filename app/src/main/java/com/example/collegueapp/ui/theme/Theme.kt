package com.example.collegueapp.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = BackgroundLight,
    onBackground = LightText,
    surface = White,
    onSurface = LightText,
    onSurfaceVariant = Slate500,
    outline = Slate200,
    secondary = Success,
    tertiary = Warning
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = White,
    background = BackgroundDark,
    onBackground = DarkText,
    surface = Slate900.copy(alpha = 0.7f),
    onSurface = DarkText,
    onSurfaceVariant = Slate400,
    outline = Slate700,
    secondary = Success,
    tertiary = Warning
)
@Composable
fun CollegueAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        colorScheme = DarkColorScheme,
        shapes = Shapes,
        typography = AppTypography,
        content = content
    )
}