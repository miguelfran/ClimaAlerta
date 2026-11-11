package br.com.fiap.climaalerta.data.repository

import br.com.fiap.climaalerta.data.local.DefaultCities
import br.com.fiap.climaalerta.data.local.FavoriteCitiesLocalDataSource
import br.com.fiap.climaalerta.domain.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Cidades favoritas (monitoradas) do usuário, observáveis por todas as telas. */
interface FavoritesRepository {
    val favorites: StateFlow<List<City>>
    fun toggleFavorite(city: City)
}

class FavoritesRepositoryImpl(
    private val localDataSource: FavoriteCitiesLocalDataSource
) : FavoritesRepository {

    private val _favorites = MutableStateFlow(loadInitialFavorites())
    override val favorites: StateFlow<List<City>> = _favorites.asStateFlow()

    override fun toggleFavorite(city: City) {
        _favorites.update { current ->
            if (current.any { it.id == city.id }) {
                current.filterNot { it.id == city.id }
            } else {
                current + city
            }
        }
        localDataSource.save(_favorites.value)
    }

    /** No primeiro uso, salva algumas capitais para o painel já começar com dados. */
    private fun loadInitialFavorites(): List<City> =
        localDataSource.load() ?: DefaultCities.cities.also { localDataSource.save(it) }
}
