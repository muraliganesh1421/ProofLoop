package com.example.proofloop.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ProofLoopColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = BgDark,
    primaryContainer = CyanGlow,
    onPrimaryContainer = CyanNeon,
    secondary = IndigoNeon,
    onSecondary = TextPrimary,
    secondaryContainer = BgCardElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = MintProof,
    onTertiary = BgDark,
    background = BgDark,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderAccent,
    error = CoralAlert,
    onError = TextPrimary
)

@Composable
fun ProofLoopTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ProofLoopColorScheme,
        typography = Typography,
        content = content
    )
}

