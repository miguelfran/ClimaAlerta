package br.com.fiap.climaalerta.domain.model

import java.time.LocalDate

data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val precipitation: Double,
    val windSpeed: Double,
    val condition: WeatherCondition,
    val isDay: Boolean
)

data class DailyForecast(
    val date: LocalDate,
    val condition: WeatherCondition,
    val maxTemperature: Double,
    val minTemperature: Double,
    val precipitationSum: Double,
    val precipitationProbability: Int,
    val maxWindSpeed: Double,
    val uvIndex: Double,
    val minHumidity: Int
)

data class WeatherForecast(
    val current: CurrentWeather,
    val daily: List<DailyForecast>
)

data class CityWeather(
    val city: City,
    val forecast: WeatherForecast,
    val alerts: List<ClimateAlert>,
    val updatedAtMillis: Long
) {
    val riskLevel: RiskLevel
        get() = alerts.maxOfOrNull { it.level } ?: RiskLevel.NONE

    val today: DailyForecast?
        get() = forecast.daily.firstOrNull()
}
