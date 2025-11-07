package lt.vitalijus.cmp_custom_pagination.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightGreenColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenSecondary,
    onPrimaryContainer = GreenOnBackground,
    secondary = GreenSecondary,
    onSecondary = GreenOnSecondary,
    secondaryContainer = GreenTertiary,
    onSecondaryContainer = GreenOnBackground,
    tertiary = GreenTertiary,
    background = GreenBackground,
    onBackground = GreenOnBackground,
    surface = GreenSurface,
    onSurface = GreenOnSurface,
    surfaceVariant = GreenSurface,
    onSurfaceVariant = GreenOnSurface
)

private val DarkGreenColorScheme = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = GreenOnBackground,
    primaryContainer = GreenSecondary,
    onPrimaryContainer = GreenOnPrimary,
    secondary = GreenSecondaryDark,
    onSecondary = GreenOnBackground,
    background = GreenBackgroundDark,
    onBackground = GreenOnPrimary,
    surface = GreenSurfaceDark,
    onSurface = GreenOnPrimary,
    surfaceVariant = GreenSurfaceDark,
    onSurfaceVariant = GreenOnPrimary
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkGreenColorScheme
    } else {
        LightGreenColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
