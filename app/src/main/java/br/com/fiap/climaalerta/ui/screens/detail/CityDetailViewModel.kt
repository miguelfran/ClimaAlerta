package br.com.fiap.climaalerta.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import br.com.fiap.climaalerta.data.repository.FavoritesRepository
import br.com.fiap.climaalerta.data.repository.WeatherRepository
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.CityWeather
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.model.loadUiState
import br.com.fiap.climaalerta.ui.navigation.CityDetailRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CityDetailUiState(
    val city: City,
    val weatherState: UiState<CityWeather> = UiState.Idle,
    val isFavorite: Boolean = false,
    val isRefreshing: Boolean = false
)

/**
 * Detalhe da cidade: lê os argumentos da rota pelo SavedStateHandle e busca a previsão.
 */
class CityDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val city: City = savedStateHandle.toRoute<CityDetailRoute>().toCity()
    private val weatherState = MutableStateFlow<UiState<CityWeather>>(UiState.Idle)
    private val isRefreshing = MutableStateFlow(false)
    private var loadJob: Job? = null

    val uiState: StateFlow<CityDetailUiState> = combine(
        weatherState,
        favoritesRepository.favorites,
        isRefreshing
    ) { weather, favorites, refreshing ->
        CityDetailUiState(
            city = city,
            weatherState = weather,
            isFavorite = favorites.any { it.id == city.id },
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CityDetailUiState(city = city)
    )

    init {
        load(forceRefresh = false)
    }

    fun refresh() = load(forceRefresh = true, fromPullToRefresh = true)

    fun retry() = load(forceRefresh = false)

    fun onToggleFavorite() {
        favoritesRepository.toggleFavorite(city)
    }

    private fun load(forceRefresh: Boolean, fromPullToRefresh: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (fromPullToRefresh) isRefreshing.value = true else weatherState.value = UiState.Loading
            weatherState.value = loadUiState { weatherRepository.getCityWeather(city, forceRefresh) }
            isRefreshing.value = false
        }
    }
}
