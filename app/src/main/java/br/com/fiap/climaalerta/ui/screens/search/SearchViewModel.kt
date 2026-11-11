package br.com.fiap.climaalerta.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.climaalerta.data.repository.CityRepository
import br.com.fiap.climaalerta.data.repository.FavoritesRepository
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.ui.model.UiState
import br.com.fiap.climaalerta.ui.model.loadUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val resultsState: UiState<List<City>> = UiState.Idle,
    val favoriteIds: Set<Long> = emptySet()
)

/**
 * Busca cidades na Geocoding API enquanto o usuário digita (com debounce).
 */
class SearchViewModel(
    private val cityRepository: CityRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    private val resultsState = MutableStateFlow<UiState<List<City>>>(UiState.Idle)
    private var searchJob: Job? = null

    val uiState: StateFlow<SearchUiState> = combine(
        resultsState,
        favoritesRepository.favorites
    ) { results, favorites ->
        SearchUiState(resultsState = results, favoriteIds = favorites.map { it.id }.toSet())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    fun onQueryChange(newQuery: String) {
        query = newQuery
        scheduleSearch(delayMillis = DEBOUNCE_MILLIS)
    }

    /** Busca imediata (botão de pesquisa do teclado). */
    fun onSearch() = scheduleSearch(delayMillis = 0)

    fun retry() = scheduleSearch(delayMillis = 0)

    fun onToggleFavorite(city: City) {
        favoritesRepository.toggleFavorite(city)
    }

    private fun scheduleSearch(delayMillis: Long) {
        searchJob?.cancel()
        val term = query.trim()
        if (term.length < MIN_QUERY_LENGTH) {
            resultsState.value = UiState.Idle
            return
        }
        searchJob = viewModelScope.launch {
            delay(delayMillis)
            resultsState.value = UiState.Loading
            resultsState.value = loadUiState { cityRepository.searchCities(term) }
        }
    }

    companion object {
        const val MIN_QUERY_LENGTH = 3
        private const val DEBOUNCE_MILLIS = 500L
    }
}
