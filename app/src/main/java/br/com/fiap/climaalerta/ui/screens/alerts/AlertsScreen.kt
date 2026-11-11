package br.com.fiap.climaalerta.ui.screens.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.AlertType
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.ui.AppViewModelProvider
import br.com.fiap.climaalerta.ui.components.AlertCard
import br.com.fiap.climaalerta.ui.components.ClimaTopAppBar
import br.com.fiap.climaalerta.ui.components.ErrorView
import br.com.fiap.climaalerta.ui.components.LoadingView
import br.com.fiap.climaalerta.ui.components.MessageView
import br.com.fiap.climaalerta.ui.components.SelectableChipRow
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.model.color
import br.com.fiap.climaalerta.ui.model.onColor
import br.com.fiap.climaalerta.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onCityClick: (City) -> Unit,
    viewModel: AlertsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ClimaTopAppBar(
                title = stringResource(R.string.alerts_title),
                subtitle = stringResource(R.string.alerts_subtitle),
                actions = {
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
            when (val state = uiState.alertsState) {
                UiState.Idle, UiState.Loading -> LoadingView(
                    message = stringResource(R.string.alerts_loading),
                    modifier = Modifier.fillMaxSize()
                )
                is UiState.Error -> ErrorView(
                    message = stringResource(state.messageRes),
                    onRetry = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
                is UiState.Success -> AlertsContent(
                    uiState = uiState,
                    onTypeSelected = viewModel::onTypeSelected,
                    onCityClick = onCityClick
                )
            }
        }
    }
}

@Composable
private fun AlertsContent(
    uiState: AlertsUiState,
    onTypeSelected: (AlertType?) -> Unit,
    onCityClick: (City) -> Unit
) {
    val horizontalPadding = Modifier.padding(horizontal = Dimens.ScreenPadding)
    val typeOptions: List<AlertType?> = listOf(null) + AlertType.entries

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)
    ) {
        item(key = "levels") {
            Row(
                modifier = horizontalPadding.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing)
            ) {
                listOf(RiskLevel.GREAT_DANGER, RiskLevel.DANGER, RiskLevel.POTENTIAL_DANGER).forEach { level ->
                    LevelCountCard(
                        level = level,
                        count = uiState.countByLevel[level] ?: 0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        item(key = "filters") {
            SelectableChipRow(
                options = typeOptions,
                selected = uiState.selectedType,
                onSelected = onTypeSelected,
                label = { type -> type?.label ?: stringResource(R.string.filter_all_alerts) }
            )
        }
        if (uiState.visibleAlerts.isEmpty()) {
            item(key = "empty") {
                MessageView(
                    icon = Icons.Filled.NotificationsOff,
                    title = stringResource(R.string.alerts_empty_title),
                    message = stringResource(R.string.alerts_empty_message),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        items(
            items = uiState.visibleAlerts,
            key = { "${it.city.id}-${it.alert.type.name}" }
        ) { cityAlert ->
            AlertCard(
                alert = cityAlert.alert,
                cityName = cityAlert.city.displayName,
                onClick = { onCityClick(cityAlert.city) },
                modifier = horizontalPadding.animateItem()
            )
        }
    }
}

@Composable
private fun LevelCountCard(level: RiskLevel, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = level.color, contentColor = level.onColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium)
            Text(
                text = level.label,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
