package br.com.fiap.climaalerta.domain.model

import java.time.LocalDate

/** Condições atuais de uma cidade. */
data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val precipitation: Double,
    val windSpeed: Double,
    val condition: WeatherCondition,
    val isDay: Boolean
)

/** Previsão consolidada de um dia. */
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

/** Previsão completa (agora + próximos dias) retornada pelo WebService. */
data class WeatherForecast(
    val current: CurrentWeather,
    val daily: List<DailyForecast>
)

/**
 * Agregado exibido nas telas: a cidade, sua previsão e os alertas avaliados pelas regras do app.
 */
data class CityWeather(
    val city: City,
    val forecast: WeatherForecast,
    val alerts: List<ClimateAlert>,
    val updatedAtMillis: Long
) {
    /** Maior nível de risco entre os alertas da cidade. */
    val riskLevel: RiskLevel
        get() = alerts.maxOfOrNull { it.level } ?: RiskLevel.NONE

    val today: DailyForecast?
        get() = forecast.daily.firstOrNull()
}
