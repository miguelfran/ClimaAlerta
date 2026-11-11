package br.com.fiap.climaalerta.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.ui.AppViewModelProvider
import br.com.fiap.climaalerta.ui.components.CityWeatherCard
import br.com.fiap.climaalerta.ui.components.ClimaTopAppBar
import br.com.fiap.climaalerta.ui.components.ErrorView
import br.com.fiap.climaalerta.ui.components.LoadingView
import br.com.fiap.climaalerta.ui.components.MessageView
import br.com.fiap.climaalerta.ui.components.RiskBadge
import br.com.fiap.climaalerta.ui.components.SearchTextField
import br.com.fiap.climaalerta.ui.components.SectionTitle
import br.com.fiap.climaalerta.ui.components.SelectableChipRow
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.theme.BrandBlue
import br.com.fiap.climaalerta.ui.theme.BrandBlueDark
import br.com.fiap.climaalerta.ui.theme.Dimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCityClick: (City) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val removedMessageTemplate = stringResource(R.string.home_city_removed)
    val undoLabel = stringResource(R.string.action_undo)

    val onRemoveCity: (City) -> Unit = { city ->
        viewModel.onToggleFavorite(city)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = removedMessageTemplate.format(city.name),
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) viewModel.onToggleFavorite(city)
        }
    }

    Scaffold(
        topBar = {
            ClimaTopAppBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.home_subtitle),
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.action_refresh))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState.citiesState) {
                UiState.Idle, UiState.Loading -> LoadingView(
                    message = stringResource(R.string.home_loading),
                    modifier = Modifier.fillMaxSize()
                )
                is UiState.Error -> ErrorView(
                    message = stringResource(state.messageRes),
                    onRetry = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
                is UiState.Success -> if (state.data.isEmpty()) {
                    MessageView(
                        icon = Icons.Filled.LocationCity,
                        title = stringResource(R.string.home_empty_title),
                        message = stringResource(R.string.home_empty_message),
                        modifier = Modifier.fillMaxSize(),
                        action = {
                            Button(onClick = onSearchClick) {
                                Icon(Icons.Filled.Search, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.home_empty_action))
                            }
                        }
                    )
                } else {
                    HomeContent(
                        uiState = uiState,
                        query = viewModel.query,
                        onQueryChange = viewModel::onQueryChange,
                        onFilterSelected = viewModel::onFilterSelected,
                        onCityClick = onCityClick,
                        onRemoveCity = onRemoveCity
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterSelected: (RiskFilter) -> Unit,
    onCityClick: (City) -> Unit,
    onRemoveCity: (City) -> Unit
) {
    val horizontalPadding = Modifier.padding(horizontal = Dimens.ScreenPadding)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = Dimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)
    ) {
        uiState.summary?.let { summary ->
            item(key = "summary") {
                RiskSummaryCard(summary = summary, modifier = horizontalPadding)
            }
        }
        item(key = "search") {
            SearchTextField(
                query = query,
                onQueryChange = onQueryChange,
                placeholder = stringResource(R.string.home_filter_placeholder),
                modifier = horizontalPadding
            )
        }
        item(key = "filters") {
            SelectableChipRow(
                options = RiskFilter.entries,
                selected = uiState.selectedFilter,
                onSelected = onFilterSelected,
                label = { filter -> filter.level?.label ?: stringResource(R.string.filter_all_cities) }
            )
        }
        item(key = "title") {
            SectionTitle(
                text = stringResource(R.string.home_section_cities, uiState.visibleCities.size),
                modifier = horizontalPadding
            )
        }
        if (uiState.visibleCities.isEmpty()) {
            item(key = "empty") {
                MessageView(
                    icon = Icons.Filled.FilterAltOff,
                    title = stringResource(R.string.home_no_match_title),
                    message = stringResource(R.string.home_no_match_message),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        items(items = uiState.visibleCities, key = { it.city.id }) { cityWeather ->
            CityWeatherCard(
                cityWeather = cityWeather,
                isFavorite = true,
                onClick = { onCityClick(cityWeather.city) },
                onToggleFavorite = { onRemoveCity(cityWeather.city) },
                modifier = horizontalPadding.animateItem()
            )
        }
    }
}

@Composable
private fun RiskSummaryCard(summary: DashboardSummary, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(BrandBlue, BrandBlueDark)))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.home_summary_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(
                    R.string.home_summary_cities_with_alerts,
                    summary.citiesWithAlerts,
                    summary.monitoredCount
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(Modifier.height(12.dp))
            if (summary.mostCriticalCity != null) {
                Text(
                    text = stringResource(R.string.home_summary_highest_risk),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RiskBadge(level = summary.highestRisk)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.home_summary_city, summary.mostCriticalCity),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.home_summary_all_clear),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
