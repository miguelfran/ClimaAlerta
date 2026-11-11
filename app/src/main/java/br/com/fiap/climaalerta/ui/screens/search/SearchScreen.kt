package br.com.fiap.climaalerta.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.ui.AppViewModelProvider
import br.com.fiap.climaalerta.ui.components.CityListItem
import br.com.fiap.climaalerta.ui.components.ClimaTopAppBar
import br.com.fiap.climaalerta.ui.components.ErrorView
import br.com.fiap.climaalerta.ui.components.LoadingView
import br.com.fiap.climaalerta.ui.components.MessageView
import br.com.fiap.climaalerta.ui.components.SearchTextField
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.theme.Dimens

@Composable
fun SearchScreen(
    onCityClick: (City) -> Unit,
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ClimaTopAppBar(title = stringResource(R.string.search_title)) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchTextField(
                query = viewModel.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = viewModel::onSearch,
                placeholder = stringResource(R.string.search_placeholder),
                modifier = Modifier.padding(Dimens.ScreenPadding)
            )
            SearchResults(
                state = uiState.resultsState,
                query = viewModel.query,
                favoriteIds = uiState.favoriteIds,
                onCityClick = onCityClick,
                onToggleFavorite = viewModel::onToggleFavorite,
                onRetry = viewModel::retry
            )
        }
    }
}

@Composable
private fun SearchResults(
    state: UiState<List<City>>,
    query: String,
    favoriteIds: Set<Long>,
    onCityClick: (City) -> Unit,
    onToggleFavorite: (City) -> Unit,
    onRetry: () -> Unit
) {
    val fullSize = Modifier.fillMaxSize()
    when (state) {
        UiState.Idle -> MessageView(
            icon = Icons.Filled.TravelExplore,
            title = stringResource(R.string.search_initial_title),
            message = stringResource(R.string.search_initial_message, SearchViewModel.MIN_QUERY_LENGTH),
            modifier = fullSize
        )
        UiState.Loading -> LoadingView(
            message = stringResource(R.string.search_loading),
            modifier = fullSize
        )
        is UiState.Error -> ErrorView(
            message = stringResource(state.messageRes),
            onRetry = onRetry,
            modifier = fullSize
        )
        is UiState.Success -> if (state.data.isEmpty()) {
            MessageView(
                icon = Icons.Filled.SearchOff,
                title = stringResource(R.string.search_empty_title),
                message = stringResource(R.string.search_empty_message, query.trim()),
                modifier = fullSize
            )
        } else {
            LazyColumn(
                modifier = fullSize,
                contentPadding = PaddingValues(
                    start = Dimens.ScreenPadding,
                    end = Dimens.ScreenPadding,
                    bottom = Dimens.ScreenPadding
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.SmallSpacing)
            ) {
                item(key = "count") {
                    Text(
                        text = pluralStringResource(R.plurals.search_results_count, state.data.size, state.data.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(items = state.data, key = { it.id }) { city ->
                    CityListItem(
                        city = city,
                        isFavorite = city.id in favoriteIds,
                        onClick = { onCityClick(city) },
                        onToggleFavorite = { onToggleFavorite(city) }
                    )
                }
            }
        }
    }
}
