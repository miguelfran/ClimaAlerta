package br.com.fiap.climaalerta.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.climaalerta.data.repository.FavoritesRepository
import br.com.fiap.climaalerta.data.repository.WeatherRepository
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.CityWeather
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.model.loadUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer

enum class RiskFilter(val level: RiskLevel?) {
    ALL(null),
    GREAT_DANGER(RiskLevel.GREAT_DANGER),
    DANGER(RiskLevel.DANGER),
    POTENTIAL_DANGER(RiskLevel.POTENTIAL_DANGER),
    NO_ALERTS(RiskLevel.NONE);

    fun matches(riskLevel: RiskLevel): Boolean = level == null || level == riskLevel
}

data class DashboardSummary(
    val monitoredCount: Int,
    val citiesWithAlerts: Int,
    val highestRisk: RiskLevel,
    val mostCriticalCity: String?
)

data class HomeUiState(
    val citiesState: UiState<List<CityWeather>> = UiState.Idle,
    val visibleCities: List<CityWeather> = emptyList(),
    val summary: DashboardSummary? = null,
    val selectedFilter: RiskFilter = RiskFilter.ALL,
    val isRefreshing: Boolean = false
)

class HomeViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    private val citiesState = MutableStateFlow<UiState<List<CityWeather>>>(UiState.Idle)
    private val selectedFilter = MutableStateFlow(RiskFilter.ALL)
    private val isRefreshing = MutableStateFlow(false)
    private var loadJob: Job? = null

    val uiState: StateFlow<HomeUiState> = combine(
        citiesState,
        snapshotFlow { query },
        selectedFilter,
        isRefreshing
    ) { state, query, filter, refreshing ->
        val cities = (state as? UiState.Success)?.data.orEmpty()
        HomeUiState(
            citiesState = state,
            visibleCities = filterCities(cities, query, filter),
            summary = if (state is UiState.Success) buildSummary(cities) else null,
            selectedFilter = filter,
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    init {
        favoritesRepository.favorites
            .onEach { cities -> loadWeather(cities, forceRefresh = false) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        query = value
    }

    fun onFilterSelected(filter: RiskFilter) {
        selectedFilter.value = filter
    }

    fun refresh() {
        loadWeather(favoritesRepository.favorites.value, forceRefresh = true, fromPullToRefresh = true)
    }

    fun retry() {
        loadWeather(favoritesRepository.favorites.value, forceRefresh = false)
    }

    fun onToggleFavorite(city: City) {
        favoritesRepository.toggleFavorite(city)
    }

    private fun loadWeather(cities: List<City>, forceRefresh: Boolean, fromPullToRefresh: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (fromPullToRefresh) {
                isRefreshing.value = true
            } else if (citiesState.value !is UiState.Success) {
                citiesState.value = UiState.Loading
            }
            citiesState.value = loadUiState {
                weatherRepository.getCitiesWeather(cities, forceRefresh)
                    .sortedByDescending { it.riskLevel }
            }
            isRefreshing.value = false
        }
    }

    private fun filterCities(cities: List<CityWeather>, query: String, filter: RiskFilter): List<CityWeather> {
        val term = query.normalizeForSearch()
        return cities.filter { cityWeather ->
            val city = cityWeather.city
            val matchesText = term.isEmpty() ||
                city.name.normalizeForSearch().contains(term) ||
                city.state.orEmpty().normalizeForSearch().contains(term)
            matchesText && filter.matches(cityWeather.riskLevel)
        }
    }

    private fun buildSummary(cities: List<CityWeather>): DashboardSummary {
        val mostCritical = cities.maxByOrNull { it.riskLevel }
        return DashboardSummary(
            monitoredCount = cities.size,
            citiesWithAlerts = cities.count { it.riskLevel != RiskLevel.NONE },
            highestRisk = mostCritical?.riskLevel ?: RiskLevel.NONE,
            mostCriticalCity = mostCritical?.takeIf { it.riskLevel != RiskLevel.NONE }?.city?.name
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

internal fun String.normalizeForSearch(): String =
    Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
