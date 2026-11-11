package br.com.fiap.climaalerta.data.repository

import br.com.fiap.climaalerta.data.remote.api.ForecastApi
import br.com.fiap.climaalerta.data.remote.mapper.toDomain
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.CityWeather
import br.com.fiap.climaalerta.domain.usecase.EvaluateClimateAlertsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

interface WeatherRepository {
    suspend fun getCityWeather(city: City, forceRefresh: Boolean = false): CityWeather
    suspend fun getCitiesWeather(cities: List<City>, forceRefresh: Boolean = false): List<CityWeather>
}

class WeatherRepositoryImpl(
    private val forecastApi: ForecastApi,
    private val evaluateClimateAlerts: EvaluateClimateAlertsUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis
) : WeatherRepository {

    private val cache = ConcurrentHashMap<Long, CityWeather>()

    override suspend fun getCityWeather(city: City, forceRefresh: Boolean): CityWeather {
        val cached = cache[city.id]
        if (!forceRefresh && cached != null && !cached.isExpired()) {
            return cached.copy(city = city)
        }
        return withContext(ioDispatcher) {
            val forecast = forecastApi.getForecast(city.latitude, city.longitude).toDomain()
            CityWeather(
                city = city,
                forecast = forecast,
                alerts = evaluateClimateAlerts(forecast),
                updatedAtMillis = currentTimeMillis()
            ).also { cache[city.id] = it }
        }
    }

    override suspend fun getCitiesWeather(cities: List<City>, forceRefresh: Boolean): List<CityWeather> =
        coroutineScope {
            cities.map { city -> async { getCityWeather(city, forceRefresh) } }.awaitAll()
        }

    private fun CityWeather.isExpired(): Boolean =
        currentTimeMillis() - updatedAtMillis > CACHE_DURATION_MILLIS

    private companion object {
        const val CACHE_DURATION_MILLIS = 10 * 60 * 1000L
    }
}
