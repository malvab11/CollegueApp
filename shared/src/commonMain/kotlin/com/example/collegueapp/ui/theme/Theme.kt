package com.example.collegueapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SchoolNavy = Color(0xFF142D4E)
val SchoolBlue = Color(0xFF2664DE)
val SchoolTeal = Color(0xFF0C8B82)
private val Light = lightColorScheme(
    primary = SchoolBlue, onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF0FF), onPrimaryContainer = Color(0xFF173E86),
    secondary = SchoolTeal, onSecondary = Color.White,
    secondaryContainer = Color(0xFFDFF5EF), onSecondaryContainer = Color(0xFF08594F),
    background = Color(0xFFF5F7FB), onBackground = Color(0xFF172A42),
    surface = Color.White, onSurface = Color(0xFF172A42),
    surfaceVariant = Color(0xFFEFF3F9), onSurfaceVariant = Color(0xFF5C6D83),
    surfaceContainer = Color.White, surfaceContainerHigh = Color(0xFFF0F4FB),
    outline = Color(0xFFBAC7DA), outlineVariant = Color(0xFFE4EAF3),
    error = Color(0xFFB72D42), errorContainer = Color(0xFFFFE9ED)
)
private val Dark = darkColorScheme(
    primary = Color(0xFF9DBDFF), onPrimary = Color(0xFF122E60),
    primaryContainer = Color(0xFF243D65), onPrimaryContainer = Color(0xFFDCE8FF),
    secondary = Color(0xFF77D8C7), secondaryContainer = Color(0xFF153E3B),
    background = Color(0xFF101A29), onBackground = Color(0xFFE7EEF9),
    surface = Color(0xFF192638), onSurface = Color(0xFFE7EEF9),
    surfaceVariant = Color(0xFF24364C), onSurfaceVariant = Color(0xFFA9B9D1),
    surfaceContainer = Color(0xFF192638), surfaceContainerHigh = Color(0xFF223249),
    outline = Color(0xFF69809F), outlineVariant = Color(0xFF304158)
)

@Composable
fun SchoolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        typography = AppTypography,
        shapes = Shapes,
        content = content
    )
}
