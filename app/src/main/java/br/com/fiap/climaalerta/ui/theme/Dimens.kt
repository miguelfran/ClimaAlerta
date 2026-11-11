package br.com.fiap.climaalerta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object Dimens {
    val ScreenPadding = 16.dp
    val ItemSpacing = 12.dp
    val SmallSpacing = 8.dp
}

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
