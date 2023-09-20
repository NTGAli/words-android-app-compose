package com.ntg.mywords.ui.theme

import android.app.Activity
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.ntg.mywords.R
import com.ntg.mywords.util.UserStore


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dataStore = UserStore(LocalContext.current)
    val userTheme =
        dataStore.getAccessToken.collectAsState(initial = stringResource(id = R.string.system_default))

    val colors = if (userTheme.value == stringResource(id = R.string.system_default)) {
        if (!useDarkTheme) {
            LightColors
        } else {
            DarkColors
        }
    } else if (userTheme.value == stringResource(id = R.string.light_mode)) {
        LightColors
    } else {
        DarkColors
    }


//    val colors = if (!useDarkTheme) {
//        LightColors
//    } else {
//        DarkColors
//    }


    val view = LocalView.current
//    (view.context as Activity).window.statusBarColor = colors.background.toArgb()
//    (view.context as Activity).window.navigationBarColor = colors.background.toArgb()
//
//    ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars =
//        userTheme.value != stringResource(
//            id = R.string.dark_mode
//        )
//    ViewCompat.getWindowInsetsController(view)?.isAppearanceLightNavigationBars =
//        userTheme.value != stringResource(
//            id = R.string.dark_mode
//        )


    val ctx = LocalContext.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
//        window.statusBarColor = colors.primary.toArgb()
//        WindowCompat
//            .getInsetsController(window, view)
//            .isAppearanceLightStatusBars = useDarkTheme
        SideEffect {
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()

            when (userTheme.value) {

                ctx.getString(R.string.system_default) -> {
                    WindowCompat
                        .getInsetsController(window, view)
                        .isAppearanceLightStatusBars = !useDarkTheme

                    WindowCompat
                        .getInsetsController(window, view)
                        .isAppearanceLightNavigationBars = !useDarkTheme

                }

                else -> {
                    WindowCompat
                        .getInsetsController(window, view)
                        .isAppearanceLightStatusBars =
                        userTheme.value != ctx.getString(
                            R.string.dark_mode
                        )

                    WindowCompat
                        .getInsetsController(window, view)
                        .isAppearanceLightNavigationBars =
                        userTheme.value != ctx.getString(
                            R.string.dark_mode
                        )
                }

            }


        }
    }

//    WindowCompat.setDecorFitsSystemWindows((view.context as Activity).window, false)
//
//
//    (view.context as Activity).window.statusBarColor = Color.Transparent.toArgb()
//    (view.context as Activity).window.navigationBarColor = Color.Transparent.toArgb()

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}