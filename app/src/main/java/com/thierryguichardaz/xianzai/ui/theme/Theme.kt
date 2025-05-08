package com.thierryguichardaz.xianzai.ui.theme // Assicurati che il package sia giusto

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Schema colori SCURO (invariato)
private val DarkColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = DarkNavy,
    primaryContainer = LightNavy,
    onPrimaryContainer = LightSlate,
    secondary = Slate,
    onSecondary = DarkNavy,
    tertiary = Slate,
    onTertiary = DarkNavy,
    background = DarkNavy,
    onBackground = LightSlate,
    surface = Navy,
    onSurface = LightSlate,
    surfaceVariant = Navy, // Usato per TopAppBar nel tuo tema scuro?
    onSurfaceVariant = Slate,
    outline = DarkSlate,
    error = Color(0xFFF2B8B5), // Errori standard M3 per tema scuro
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
    // inversePrimary = ..., surfaceTint = ..., etc. (puoi definirli se necessario)
)

// Schema colori CHIARO (MODIFICATO usando i nuovi colori)
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,            // Colore principale (blu)
    onPrimary = Color.White,          // Testo su primario (bianco)
    primaryContainer = Color(0xFFCCE5FF), // Sfondo opzione selezionata (azzurrino chiaro)
    onPrimaryContainer = Color(0xFF001A33), // Testo su opzione selezionata (blu scuro)
    secondary = MediumGrayText,       // Colore secondario (grigio medio)
    onSecondary = Color.White,
    tertiary = MediumGrayText,        // Colore terziario
    onTertiary = Color.White,
    background = LightBackground,     // Sfondo generale (bianco sporco)
    onBackground = DarkText,          // Testo su sfondo (nero/grigio scuro)
    surface = LightSurface,           // Sfondo Card (bianco puro)
    onSurface = DarkText,             // Testo su Card
    surfaceVariant = LightSurfaceVariant, // Variante superficie (es. sfondo TopAppBar grigio chiaro)
    onSurfaceVariant = MediumGrayText,  // Testo secondario su superfici varianti
    outline = LightGrayBorder,        // Bordo (grigio chiaro)
    error = Color(0xFFB3261E),        // Errori standard M3 per tema chiaro
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
    // inversePrimary = ..., surfaceTint = ..., etc. (puoi definirli se necessario)
)

@Composable
fun XianZaiTheme( // MODIFICATO: Ora usa il tema di sistema di default
    darkTheme: Boolean = isSystemInDarkTheme(), // <--- Usa l'impostazione di sistema!
    // Dynamic color (Android 12+) lo manteniamo disabilitato per coerenza
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Sceglie lo schema colori corretto basato su darkTheme
    val colorScheme = when {
        // dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        //    val context = LocalContext.current
        //    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        // } // Logica Dynamic Color (commentata perché dynamicColor = false)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Imposta il colore della status bar allo sfondo del tema corrente
            @Suppress("DEPRECATION") // Add this annotation
            window.statusBarColor = colorScheme.background.toArgb()
            // Imposta le icone della status bar: chiare su sfondo scuro, scure su sfondo chiaro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // (Opzionale) Imposta colore barra di navigazione (se visibile)
            // window.navigationBarColor = colorScheme.surfaceColorAtElevation(3.dp).toArgb() // Esempio
            // WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme // Per icone nav bar
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usa la Typography da Type.kt
        content = content
    )
}

// Non serve definire Typography qui se è già definita correttamente in Type.kt
// Assicurati che `import androidx.compose.material3.Typography` sia presente se
// usi `val Typography = Typography()` (default) in Type.kt
// o che `import com.thierryguichardaz.xianzai.ui.theme.Typography` se hai una definizione custom
// come quella che hai fornito.