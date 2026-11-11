package br.com.fiap.climaalerta.data.remote.mapper

import br.com.fiap.climaalerta.data.remote.dto.CurrentWeatherDto
import br.com.fiap.climaalerta.data.remote.dto.DailyForecastDto
import br.com.fiap.climaalerta.data.remote.dto.ForecastResponseDto
import br.com.fiap.climaalerta.data.remote.dto.GeocodingResultDto
import br.com.fiap.climaalerta.domain.model.City
import br.com.fiap.climaalerta.domain.model.CurrentWeather
import br.com.fiap.climaalerta.domain.model.DailyForecast
import br.com.fiap.climaalerta.domain.model.WeatherCondition
import br.com.fiap.climaalerta.domain.model.WeatherForecast
import java.time.LocalDate
import kotlin.math.roundToInt

class InvalidWeatherResponseException(message: String) : IllegalStateException(message)

fun ForecastResponseDto.toDomain(): WeatherForecast {
    val currentDto = current ?: throw InvalidWeatherResponseException("Resposta sem dados atuais")
    val dailyDto = daily ?: throw InvalidWeatherResponseException("Resposta sem previsão diária")
    return WeatherForecast(
        current = currentDto.toDomain(),
        daily = dailyDto.toDomain()
    )
}

private fun CurrentWeatherDto.toDomain() = CurrentWeather(
    temperature = temperature ?: 0.0,
    apparentTemperature = apparentTemperature ?: temperature ?: 0.0,
    humidity = relativeHumidity?.roundToInt() ?: 0,
    precipitation = precipitation ?: 0.0,
    windSpeed = windSpeed ?: 0.0,
    condition = WeatherCondition.fromWmoCode(weatherCode),
    isDay = isDay != 0
)

private fun DailyForecastDto.toDomain(): List<DailyForecast> {
    val dates = time.orEmpty()
    return dates.mapIndexed { index, isoDate ->
        DailyForecast(
            date = LocalDate.parse(isoDate),
            condition = WeatherCondition.fromWmoCode(weatherCode?.getOrNull(index)),
            maxTemperature = temperatureMax.valueAt(index),
            minTemperature = temperatureMin.valueAt(index),
            precipitationSum = precipitationSum.valueAt(index),
            precipitationProbability = precipitationProbabilityMax.valueAt(index).roundToInt(),
            maxWindSpeed = windSpeedMax.valueAt(index),
            uvIndex = uvIndexMax.valueAt(index),
            minHumidity = relativeHumidityMin?.getOrNull(index)?.roundToInt() ?: 100
        )
    }
}

private fun List<Double?>?.valueAt(index: Int): Double = this?.getOrNull(index) ?: 0.0

fun GeocodingResultDto.toDomainOrNull(): City? {
    val safeId = id ?: return null
    val safeName = name ?: return null
    val safeLatitude = latitude ?: return null
    val safeLongitude = longitude ?: return null
    return City(
        id = safeId,
        name = safeName,
        state = state,
        country = country,
        latitude = safeLatitude,
        longitude = safeLongitude,
        population = population
    )
}
