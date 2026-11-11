package br.com.fiap.climaalerta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Espaçamentos padrão das telas. */
object Dimens {
    val ScreenPadding = 16.dp
    val ItemSpacing = 12.dp
    val SmallSpacing = 8.dp
}

/** Cores da barra superior: azul da marca no tema claro e azul escuro no tema escuro. */
object BarColors {
    val container: Color
        @Composable get() = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.primary
        }

    val content: Color
        @Composable get() = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onPrimary
        }
}
