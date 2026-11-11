package br.com.fiap.climaalerta.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Resposta do endpoint GET /v1/forecast da Open-Meteo.
 * Os campos são anuláveis porque o Gson não respeita a nulidade do Kotlin.
 */
data class ForecastResponseDto(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("current") val current: CurrentWeatherDto?,
    @SerializedName("daily") val daily: DailyForecastDto?
)

data class CurrentWeatherDto(
    @SerializedName("time") val time: String?,
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("relative_humidity_2m") val relativeHumidity: Double?,
    @SerializedName("apparent_temperature") val apparentTemperature: Double?,
    @SerializedName("precipitation") val precipitation: Double?,
    @SerializedName("weather_code") val weatherCode: Int?,
    @SerializedName("wind_speed_10m") val windSpeed: Double?,
    @SerializedName("is_day") val isDay: Int?
)

/** A Open-Meteo devolve a previsão diária em listas paralelas (uma posição por dia). */
data class DailyForecastDto(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("weather_code") val weatherCode: List<Int?>?,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double?>?,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double?>?,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double?>?,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Double?>?,
    @SerializedName("wind_speed_10m_max") val windSpeedMax: List<Double?>?,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double?>?,
    @SerializedName("relative_humidity_2m_min") val relativeHumidityMin: List<Double?>?
)
