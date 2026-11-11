package br.com.fiap.climaalerta.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.domain.model.WeatherCondition
import br.com.fiap.climaalerta.ui.model.color
import br.com.fiap.climaalerta.ui.model.icon
import br.com.fiap.climaalerta.ui.model.onColor
import br.com.fiap.climaalerta.ui.theme.BrandAmber

/** Selo colorido com o nível de risco (escala INMET). */
@Composable
fun RiskBadge(level: RiskLevel, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = level.color,
        contentColor = level.onColor,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(level.icon(), contentDescription = null, modifier = Modifier.size(14.dp))
            Text(text = level.label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Ícone dentro de um círculo colorido; usado para clima, alertas e locais. */
@Composable
fun CircleIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/** Ícone da condição do tempo em destaque. */
@Composable
fun WeatherIconBadge(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    isDay: Boolean = true,
    size: Dp = 48.dp
) {
    CircleIcon(
        icon = condition.icon(isDay),
        contentDescription = condition.description,
        modifier = modifier,
        size = size
    )
}

/** Botão de favoritar com animação e mudança de cor/ícone conforme o estado. */
@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    unselectedTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.15f else 1f,
        label = "favoriteScale"
    )
    IconButton(onClick = onToggle, modifier = modifier) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
            contentDescription = stringResource(
                if (isFavorite) R.string.action_unfavorite else R.string.action_favorite
            ),
            tint = if (isFavorite) BrandAmber else unselectedTint,
            modifier = Modifier.scale(scale)
        )
    }
}
