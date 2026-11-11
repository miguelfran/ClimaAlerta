package br.com.fiap.climaalerta.data.remote.api

import br.com.fiap.climaalerta.data.remote.dto.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = FORECAST_DAYS
    ): ForecastResponseDto

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"
        const val FORECAST_DAYS = 7

        private const val CURRENT_FIELDS = "temperature_2m,relative_humidity_2m,apparent_temperature," +
            "precipitation,weather_code,wind_speed_10m,is_day"

        private const val DAILY_FIELDS = "weather_code,temperature_2m_max,temperature_2m_min," +
            "precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max," +
            "relative_humidity_2m_min"
    }
}
