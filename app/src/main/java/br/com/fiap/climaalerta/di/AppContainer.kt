package br.com.fiap.climaalerta.di

import android.content.Context
import br.com.fiap.climaalerta.data.local.FavoriteCitiesLocalDataSource
import br.com.fiap.climaalerta.data.local.SafetyGuideLocalDataSource
import br.com.fiap.climaalerta.data.local.UserPreferences
import br.com.fiap.climaalerta.data.remote.RetrofitFactory
import br.com.fiap.climaalerta.data.remote.api.ForecastApi
import br.com.fiap.climaalerta.data.remote.api.GeocodingApi
import br.com.fiap.climaalerta.data.repository.CityRepository
import br.com.fiap.climaalerta.data.repository.CityRepositoryImpl
import br.com.fiap.climaalerta.data.repository.FavoritesRepository
import br.com.fiap.climaalerta.data.repository.FavoritesRepositoryImpl
import br.com.fiap.climaalerta.data.repository.SafetyGuideRepository
import br.com.fiap.climaalerta.data.repository.SafetyGuideRepositoryImpl
import br.com.fiap.climaalerta.data.repository.WeatherRepository
import br.com.fiap.climaalerta.data.repository.WeatherRepositoryImpl
import br.com.fiap.climaalerta.domain.usecase.EvaluateClimateAlertsUseCase

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val forecastApi: ForecastApi by lazy {
        RetrofitFactory.createService(ForecastApi.BASE_URL, ForecastApi::class.java)
    }

    private val geocodingApi: GeocodingApi by lazy {
        RetrofitFactory.createService(GeocodingApi.BASE_URL, GeocodingApi::class.java)
    }

    val userPreferences: UserPreferences by lazy { UserPreferences(appContext) }

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(forecastApi, EvaluateClimateAlertsUseCase())
    }

    val cityRepository: CityRepository by lazy { CityRepositoryImpl(geocodingApi) }

    val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepositoryImpl(FavoriteCitiesLocalDataSource(appContext))
    }

    val safetyGuideRepository: SafetyGuideRepository by lazy {
        SafetyGuideRepositoryImpl(SafetyGuideLocalDataSource())
    }
}
