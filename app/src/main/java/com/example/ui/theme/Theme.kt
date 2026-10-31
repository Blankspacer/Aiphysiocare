package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = PhysioDarkPrimary,
  onPrimary = Color.Black,
  primaryContainer = PhysioTealDark,
  onPrimaryContainer = PhysioDarkPrimary,
  secondary = PhysioMintAccent,
  onSecondary = Color.Black,
  background = PhysioDarkBg,
  surface = PhysioDarkSurface,
  surfaceVariant = PhysioDarkCard,
  onBackground = PhysioDarkText,
  onSurface = PhysioDarkText,
  error = PhysioError
)

private val LightColorScheme = lightColorScheme(
  primary = PhysioTealPrimary,
  onPrimary = Color.White,
  primaryContainer = PhysioCyanContainer,
  onPrimaryContainer = PhysioOnCyanContainer,
  secondary = PhysioMintAccent,
  onSecondary = Color.White,
  background = PhysioNavySurface,
  surface = PhysioCardBg,
  surfaceVariant = Color(0xFFF0F5F9),
  onBackground = PhysioTextDark,
  onSurface = PhysioTextDark,
  outline = PhysioDivider,
  error = PhysioError
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent medical teal branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
