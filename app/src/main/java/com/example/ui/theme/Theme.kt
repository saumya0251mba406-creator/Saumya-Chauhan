package com.example.ui.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val NivoraLightColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = Color.White,
    primaryContainer = SageContainer,
    onPrimaryContainer = OnSageContainer,
    secondary = WarmTaupeSecondary,
    onSecondary = Color.White,
    secondaryContainer = WarmTaupeContainer,
    onSecondaryContainer = OnWarmTaupeContainer,
    tertiary = SagePrimaryDark,
    onTertiary = Color.White,
    background = WarmOffWhite,
    onBackground = CharcoalText,
    surface = PureCardWhite,
    onSurface = CharcoalText,
    surfaceVariant = SoftBeige,
    onSurfaceVariant = MutedCharcoalText,
    surfaceContainerLow = WarmOffWhite,
    surfaceContainer = SoftBeige,
    surfaceContainerHigh = SoftBeigeHigh,
    outline = WarmTaupeSecondary,
    outlineVariant = SubtleSandBorder,
    error = ErrorCrimson,
    onError = Color.White,
    errorContainer = ErrorSoftContainer,
    onErrorContainer = OnErrorSoftContainer,
)

private val NivoraDarkColorScheme = darkColorScheme(
    primary = DarkSagePrimary,
    onPrimary = DarkOnSagePrimary,
    primaryContainer = DarkSageContainer,
    onPrimaryContainer = DarkCharcoalText,
    secondary = WarmTaupeContainer,
    onSecondary = OnWarmTaupeContainer,
    secondaryContainer = DarkSurfaceHigh,
    onSecondaryContainer = DarkCharcoalText,
    background = DarkWarmBg,
    onBackground = DarkCharcoalText,
    surface = DarkSurface,
    onSurface = DarkCharcoalText,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = DarkMutedText,
    surfaceContainerLow = DarkWarmBg,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceHigh,
    outline = DarkMutedText,
    outlineVariant = DarkBorder,
    error = Color(0xFFE58C8C),
    onError = Color(0xFF471212),
    errorContainer = Color(0xFF5D2121),
    onErrorContainer = Color(0xFFFBEAEA),
)

val NivoraShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun NivoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val reducedMotion = remember(context) {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f,
            )
            scale == 0f
        } catch (_: Throwable) {
            false
        }
    }

    val colorScheme = if (darkTheme) NivoraDarkColorScheme else NivoraLightColorScheme

    CompositionLocalProvider(LocalReducedMotion provides reducedMotion) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NivoraTypography,
            shapes = NivoraShapes,
            content = content,
        )
    }
}
