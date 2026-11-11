package br.com.fiap.climaalerta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.CurrentWeather
import br.com.fiap.climaalerta.domain.model.DailyForecast
import br.com.fiap.climaalerta.ui.model.icon
import br.com.fiap.climaalerta.ui.model.toDayLabel
import br.com.fiap.climaalerta.ui.model.toPercentLabel
import br.com.fiap.climaalerta.ui.model.toPrecipitationLabel
import br.com.fiap.climaalerta.ui.model.toTemperatureLabel
import br.com.fiap.climaalerta.ui.model.toTimeLabel
import br.com.fiap.climaalerta.ui.model.toWindLabel
import br.com.fiap.climaalerta.ui.theme.BrandAmber
import br.com.fiap.climaalerta.ui.theme.BrandBlue
import br.com.fiap.climaalerta.ui.theme.BrandBlueDark
import kotlin.math.roundToInt

/** Card de destaque com o tempo atual da cidade. */
@Composable
fun CurrentWeatherCard(
    current: CurrentWeather,
    today: DailyForecast?,
    updatedAtMillis: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(BrandBlue, BrandBlueDark)))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.detail_now),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = current.temperature.toTemperatureLabel(),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White
                    )
                    Text(
                        text = current.condition.description,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(
                            R.string.detail_feels_like,
                            current.apparentTemperature.toTemperatureLabel()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Icon(
                    imageVector = current.condition.icon(current.isDay),
                    contentDescription = current.condition.description,
                    tint = BrandAmber,
                    modifier = Modifier.size(88.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Filled.WaterDrop,
                    label = stringResource(R.string.metric_humidity),
                    value = current.humidity.toPercentLabel()
                )
                MetricItem(
                    icon = Icons.Filled.Air,
                    label = stringResource(R.string.metric_wind),
                    value = current.windSpeed.toWindLabel()
                )
                MetricItem(
                    icon = Icons.Filled.Umbrella,
                    label = stringResource(R.string.metric_rain_today),
                    value = (today?.precipitationSum ?: current.precipitation).toPrecipitationLabel()
                )
                MetricItem(
                    icon = Icons.Filled.WbSunny,
                    label = stringResource(R.string.metric_uv),
                    value = (today?.uvIndex ?: 0.0).roundToInt().toString()
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.detail_updated_at, updatedAtMillis.toTimeLabel()),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}

/** Indicador pequeno (ícone + valor + rótulo). */
@Composable
fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleSmall, color = contentColor)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor.copy(alpha = 0.8f)
        )
    }
}

/** Lista da previsão diária agrupada em um card. */
@Composable
fun DailyForecastCard(days: List<DailyForecast>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            days.forEachIndexed { index, day ->
                DailyForecastRow(forecast = day)
                if (index < days.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                }
            }
        }
    }
}

/** Linha com a previsão de um dia. */
@Composable
fun DailyForecastRow(forecast: DailyForecast, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = forecast.date.toDayLabel(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1.4f)
        )
        Icon(
            imageVector = forecast.condition.icon(),
            contentDescription = forecast.condition.description,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = stringResource(R.string.metric_rain_probability),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = forecast.precipitationProbability.toPercentLabel(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = forecast.minTemperature.toTemperatureLabel(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = " / ", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = forecast.maxTemperature.toTemperatureLabel(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
