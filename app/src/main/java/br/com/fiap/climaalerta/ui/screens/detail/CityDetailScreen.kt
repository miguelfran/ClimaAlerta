package br.com.fiap.climaalerta.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.CityWeather
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.ui.AppViewModelProvider
import br.com.fiap.climaalerta.ui.components.AlertCard
import br.com.fiap.climaalerta.ui.components.ClimaTopAppBar
import br.com.fiap.climaalerta.ui.components.CurrentWeatherCard
import br.com.fiap.climaalerta.ui.components.DailyForecastCard
import br.com.fiap.climaalerta.ui.components.ErrorView
import br.com.fiap.climaalerta.ui.components.FavoriteIconButton
import br.com.fiap.climaalerta.ui.components.LoadingView
import br.com.fiap.climaalerta.ui.components.SectionTitle
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.model.color
import br.com.fiap.climaalerta.ui.model.icon
import br.com.fiap.climaalerta.ui.model.onColor
import br.com.fiap.climaalerta.ui.theme.BarColors
import br.com.fiap.climaalerta.ui.theme.Dimens

/**
 * Detalhe da cidade: tempo atual, alertas ativos com orientações e previsão de 7 dias.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CityDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val city = uiState.city

    Scaffold(
        topBar = {
            ClimaTopAppBar(
                title = city.name,
                subtitle = listOfNotNull(city.state, city.country).joinToString(" • "),
                onNavigateBack = onNavigateBack,
                actions = {
                    FavoriteIconButton(
                        isFavorite = uiState.isFavorite,
                        onToggle = viewModel::onToggleFavorite,
                        unselectedTint = BarColors.content
                    )
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.action_refresh))
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState.weatherState) {
                UiState.Idle, UiState.Loading -> LoadingView(
                    message = stringResource(R.string.detail_loading, city.name),
                    modifier = Modifier.fillMaxSize()
                )
                is UiState.Error -> ErrorView(
                    message = stringResource(state.messageRes),
                    onRetry = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
                is UiState.Success -> CityDetailContent(cityWeather = state.data)
            }
        }
    }
}

@Composable
private fun CityDetailContent(cityWeather: CityWeather) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)
    ) {
        item(key = "current") {
            CurrentWeatherCard(
                current = cityWeather.forecast.current,
                today = cityWeather.today,
                updatedAtMillis = cityWeather.updatedAtMillis
            )
        }
        item(key = "risk") {
            RiskStatusBanner(level = cityWeather.riskLevel, alertCount = cityWeather.alerts.size)
        }
        item(key = "alertsTitle") {
            SectionTitle(text = stringResource(R.string.detail_alerts_title))
        }
        if (cityWeather.alerts.isEmpty()) {
            item(key = "noAlerts") {
                Text(
                    text = stringResource(R.string.detail_no_alerts),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(items = cityWeather.alerts, key = { it.type.name }) { alert ->
                AlertCard(alert = alert)
            }
        }
        item(key = "forecastTitle") {
            SectionTitle(text = stringResource(R.string.detail_forecast_title))
        }
        item(key = "forecast") {
            DailyForecastCard(days = cityWeather.forecast.daily)
        }
        item(key = "source") {
            Text(
                text = stringResource(R.string.detail_data_source),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Faixa com a situação geral da cidade na cor do nível de risco. */
@Composable
private fun RiskStatusBanner(level: RiskLevel, alertCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = level.color, contentColor = level.onColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (level == RiskLevel.NONE) Icons.Filled.CheckCircle else level.icon(),
                contentDescription = null
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (level == RiskLevel.NONE) {
                    stringResource(R.string.detail_status_safe)
                } else {
                    pluralStringResource(R.plurals.detail_status_alerts, alertCount, level.label, alertCount)
                },
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
