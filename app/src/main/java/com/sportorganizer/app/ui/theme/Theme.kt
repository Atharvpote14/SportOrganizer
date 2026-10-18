
package com.sportorganizer.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SportBlueLight,
    onPrimary = SportNavy,
    secondary = SportCyan,
    onSecondary = SportNavy,
    tertiary = SportSuccess,
    background = SportBackgroundDark,
    onBackground = SportTextDark,
    surface = SportSurfaceDark,
    onSurface = SportTextDark,
    surfaceVariant = SportSurfaceVariantDark,
    onSurfaceVariant = SportTextSecondaryDark,
    outline = SportOutlineDark,
    error = SportError,
    onError = SportOnError
)

private val LightColorScheme = lightColorScheme(
    primary = SportBlue,
    onPrimary = SportOnError,
    secondary = SportCyan,
    onSecondary = SportNavy,
    tertiary = SportSuccess,
    background = SportBackgroundLight,
    onBackground = SportTextLight,
    surface = SportSurfaceLight,
    onSurface = SportTextLight,
    surfaceVariant = SportSurfaceVariantLight,
    onSurfaceVariant = SportTextSecondaryLight,
    outline = SportOutlineLight,
    error = SportError,
    onError = SportOnError
)

private val SportOrganizerShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SportOrganizerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SportOrganizerShapes,
        content = content
    )
}
