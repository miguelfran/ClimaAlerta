package br.com.fiap.climaalerta.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.CityWeather
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.ui.model.color
import br.com.fiap.climaalerta.ui.model.toPopulationLabel
import br.com.fiap.climaalerta.ui.model.toTemperatureLabel

/**
 * Card de uma cidade monitorada com tempo atual, máxima/mínima e nível de risco.
 * Clique abre o detalhe; a estrela favorita/desfavorita.
 */
@Composable
fun CityWeatherCard(
    cityWeather: CityWeather,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val city = cityWeather.city
    val current = cityWeather.forecast.current
    val today = cityWeather.today
    val risk = cityWeather.riskLevel

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (risk != RiskLevel.NONE) BorderStroke(1.5.dp, risk.color) else null
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIconBadge(condition = current.condition, isDay = current.isDay, size = 52.dp)
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = current.condition.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RiskBadge(level = risk)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = current.temperature.toTemperatureLabel(),
                    style = MaterialTheme.typography.headlineMedium
                )
                if (today != null) {
                    Text(
                        text = stringResource(
                            R.string.temperature_range,
                            today.maxTemperature.toTemperatureLabel(),
                            today.minTemperature.toTemperatureLabel()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (cityWeather.alerts.isNotEmpty()) {
                    Text(
                        text = pluralStringResource(
                            R.plurals.alerts_count,
                            cityWeather.alerts.size,
                            cityWeather.alerts.size
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FavoriteIconButton(isFavorite = isFavorite, onToggle = onToggleFavorite)
        }
    }
}

/** Item de resultado da busca de cidades. */
@Composable
fun CityListItem(
    city: City,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIcon(
                icon = Icons.Filled.LocationOn,
                contentDescription = null,
                size = 40.dp,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOfNotNull(city.state, city.country).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                city.population?.takeIf { it > 0 }?.let { population ->
                    Text(
                        text = stringResource(R.string.search_population, population.toPopulationLabel()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            FavoriteIconButton(isFavorite = isFavorite, onToggle = onToggleFavorite)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
