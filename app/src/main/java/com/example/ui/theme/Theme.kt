package com.example.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

data class SecureColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val ink: Color,
    val muted: Color,
    val divider: Color
)

val LocalSecureColors = staticCompositionLocalOf {
    SecureColors(
        isDark = false,
        background = SecureBackgroundLight,
        surface = SecureSurfaceLight,
        surfaceSoft = SecureSurfaceSoftLight,
        ink = SecureInkLight,
        muted = SecureMutedLight,
        divider = SecureDividerLight
    )
}

val SecureBackground: Color
    @Composable
    get() = LocalSecureColors.current.background

val SecureSurface: Color
    @Composable
    get() = LocalSecureColors.current.surface

val SecureSurfaceSoft: Color
    @Composable
    get() = LocalSecureColors.current.surfaceSoft

val SecureInk: Color
    @Composable
    get() = LocalSecureColors.current.ink

val SecureMuted: Color
    @Composable
    get() = LocalSecureColors.current.muted

val SecureDivider: Color
    @Composable
    get() = LocalSecureColors.current.divider

private val DarkColorScheme = darkColorScheme(
    primary = SecurePrimaryLight,
    onPrimary = Color.White,
    background = SecureBackgroundDark,
    surface = SecureSurfaceDark,
    onBackground = SecureInkDark,
    onSurface = SecureInkDark
)

private val LightColorScheme = lightColorScheme(
    primary = SecurePrimary,
    onPrimary = Color.White,
    background = SecureBackgroundLight,
    surface = SecureSurfaceLight,
    onBackground = SecureInkLight,
    onSurface = SecureInkLight
)

@Composable
fun SecureLensTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val secureColors = if (darkTheme) {
        SecureColors(
            isDark = true,
            background = SecureBackgroundDark,
            surface = SecureSurfaceDark,
            surfaceSoft = SecureSurfaceSoftDark,
            ink = SecureInkDark,
            muted = SecureMutedDark,
            divider = SecureDividerDark
        )
    } else {
        SecureColors(
            isDark = false,
            background = SecureBackgroundLight,
            surface = SecureSurfaceLight,
            surfaceSoft = SecureSurfaceSoftLight,
            ink = SecureInkLight,
            muted = SecureMutedLight,
            divider = SecureDividerLight
        )
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSecureColors provides secureColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
