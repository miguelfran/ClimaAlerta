package br.com.fiap.climaalerta.data.repository

import br.com.fiap.climaalerta.data.remote.api.GeocodingApi
import br.com.fiap.climaalerta.data.remote.mapper.toDomainOrNull
import br.com.fiap.climaalerta.domain.model.City
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface CityRepository {
    suspend fun searchCities(query: String): List<City>
}

class CityRepositoryImpl(
    private val geocodingApi: GeocodingApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CityRepository {

    override suspend fun searchCities(query: String): List<City> = withContext(ioDispatcher) {
        geocodingApi.searchCities(query.trim())
            .results
            .orEmpty()
            .filter { it.featureCode?.startsWith(POPULATED_PLACE_PREFIX) ?: true }
            .mapNotNull { it.toDomainOrNull() }
            .distinctBy { it.id }
    }

    private companion object {
        const val POPULATED_PLACE_PREFIX = "PPL"
    }
}
