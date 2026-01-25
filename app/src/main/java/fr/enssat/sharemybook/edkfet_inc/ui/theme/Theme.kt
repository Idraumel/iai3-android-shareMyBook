package fr.enssat.sharemybook.edkfet_inc.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = BookBlueDark,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF003258),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF004A77),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD1E4FF),

    secondary = BookBlueVariantDark,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF003258),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF004A77),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFD1E4FF),

    tertiary = BookOrangeDark,
    onTertiary = androidx.compose.ui.graphics.Color(0xFF452B00),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF633F00),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDDB3),

    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF93000A),
    onError = androidx.compose.ui.graphics.Color(0xFF690005),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),

    background = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    surface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E2E6),

    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF42474E),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC2C7CF),
    outline = androidx.compose.ui.graphics.Color(0xFF8C9199)
)

private val LightColorScheme = lightColorScheme(
    primary = BookBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD1E4FF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF001D36),

    secondary = BookBlueVariant,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFD1E4FF),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF001D36),

    tertiary = BookOrange,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDDB3),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF2A1800),

    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    onError = androidx.compose.ui.graphics.Color.White,
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410002),

    background = androidx.compose.ui.graphics.Color(0xFFFDFCFF),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surface = androidx.compose.ui.graphics.Color(0xFFFDFCFF),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),

    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFDFE2EB),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF42474E),
    outline = androidx.compose.ui.graphics.Color(0xFF73777F)
)

@Composable
fun Iai3androidshareMyBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom colors
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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}