package br.com.fiap.climaalerta.ui.screens.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.climaalerta.data.repository.FavoritesRepository
import br.com.fiap.climaalerta.data.repository.WeatherRepository
import br.com.fiap.climaalerta.domain.model.AlertType
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.CityAlert
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

data class AlertsUiState(
    val alertsState: UiState<List<CityAlert>> = UiState.Idle,
    val visibleAlerts: List<CityAlert> = emptyList(),
    val selectedType: AlertType? = null,
    val countByLevel: Map<RiskLevel, Int> = emptyMap(),
    val isRefreshing: Boolean = false
)

/**
 * Central de alertas: reúne os alertas de todas as cidades monitoradas,
 * ordenados do mais grave para o menos grave, com filtro por tipo de evento.
 */
class AlertsViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val alertsState = MutableStateFlow<UiState<List<CityAlert>>>(UiState.Idle)
    private val selectedType = MutableStateFlow<AlertType?>(null)
    private val isRefreshing = MutableStateFlow(false)
    private var loadJob: Job? = null

    val uiState: StateFlow<AlertsUiState> = combine(
        alertsState,
        selectedType,
        isRefreshing
    ) { state, type, refreshing ->
        val alerts = (state as? UiState.Success)?.data.orEmpty()
        AlertsUiState(
            alertsState = state,
            visibleAlerts = alerts.filter { type == null || it.alert.type == type },
            selectedType = type,
            countByLevel = alerts.groupingBy { it.alert.level }.eachCount(),
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AlertsUiState()
    )

    init {
        favoritesRepository.favorites
            .onEach { cities -> loadAlerts(cities, forceRefresh = false) }
            .launchIn(viewModelScope)
    }

    fun onTypeSelected(type: AlertType?) {
        selectedType.value = type
    }

    fun refresh() {
        loadAlerts(favoritesRepository.favorites.value, forceRefresh = true, fromPullToRefresh = true)
    }

    fun retry() {
        loadAlerts(favoritesRepository.favorites.value, forceRefresh = false)
    }

    private fun loadAlerts(cities: List<City>, forceRefresh: Boolean, fromPullToRefresh: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (fromPullToRefresh) {
                isRefreshing.value = true
            } else if (alertsState.value !is UiState.Success) {
                alertsState.value = UiState.Loading
            }
            alertsState.value = loadUiState {
                weatherRepository.getCitiesWeather(cities, forceRefresh)
                    .flatMap { cityWeather -> cityWeather.alerts.map { CityAlert(cityWeather.city, it) } }
                    .sortedWith(
                        compareByDescending<CityAlert> { it.alert.level }
                            .thenBy { it.alert.startDate }
                            .thenBy { it.city.name }
                    )
            }
            isRefreshing.value = false
        }
    }
}
