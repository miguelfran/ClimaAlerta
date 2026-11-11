package br.com.fiap.climaalerta.domain

import br.com.fiap.climaalerta.domain.model.AlertType
import br.com.fiap.climaalerta.domain.model.CurrentWeather
import br.com.fiap.climaalerta.domain.model.DailyForecast
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.domain.model.WeatherCondition
import br.com.fiap.climaalerta.domain.model.WeatherForecast
import br.com.fiap.climaalerta.domain.usecase.EvaluateClimateAlertsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class EvaluateClimateAlertsUseCaseTest {

    private val evaluate = EvaluateClimateAlertsUseCase()
    private val today = LocalDate.of(2026, 11, 11)

    @Test
    fun `dia ameno nao gera alertas`() {
        val alerts = evaluate(forecastOf(day()))

        assertTrue(alerts.isEmpty())
    }

    @Test
    fun `chuva acima de 50 mm gera alerta de perigo`() {
        val alerts = evaluate(forecastOf(day(precipitation = 62.0)))

        assertEquals(1, alerts.size)
        assertEquals(AlertType.HEAVY_RAIN, alerts.first().type)
        assertEquals(RiskLevel.DANGER, alerts.first().level)
    }

    @Test
    fun `calor em varios dias e consolidado em um alerta com o pico e o periodo`() {
        val alerts = evaluate(
            forecastOf(
                day(offset = 0, maxTemperature = 35.5),
                day(offset = 1, maxTemperature = 38.4),
                day(offset = 2, maxTemperature = 36.0)
            )
        )

        val heat = alerts.single { it.type == AlertType.EXTREME_HEAT }
        assertEquals(RiskLevel.DANGER, heat.level)
        assertEquals(38.4, heat.peakValue, 0.001)
        assertEquals(today, heat.startDate)
        assertEquals(today.plusDays(2), heat.endDate)
        assertEquals(3, heat.activeDays)
    }

    @Test
    fun `evento em dias alternados conta apenas os dias afetados`() {
        val alerts = evaluate(
            forecastOf(
                day(offset = 0, condition = WeatherCondition.THUNDERSTORM),
                day(offset = 1),
                day(offset = 2, condition = WeatherCondition.THUNDERSTORM)
            )
        )

        val storm = alerts.single { it.type == AlertType.STORM }
        assertEquals(2, storm.activeDays)
        assertEquals(today.plusDays(2), storm.endDate)
    }

    @Test
    fun `frio considera a menor temperatura como pico`() {
        val alerts = evaluate(
            forecastOf(day(offset = 0, minTemperature = 4.0), day(offset = 1, minTemperature = 1.0))
        )

        val cold = alerts.single { it.type == AlertType.INTENSE_COLD }
        assertEquals(RiskLevel.DANGER, cold.level)
        assertEquals(1.0, cold.peakValue, 0.001)
    }

    @Test
    fun `tempestade com granizo e mais grave que tempestade comum`() {
        val storm = evaluate(forecastOf(day(condition = WeatherCondition.THUNDERSTORM))).single()
        val hail = evaluate(forecastOf(day(condition = WeatherCondition.THUNDERSTORM_WITH_HAIL))).single()

        assertEquals(RiskLevel.POTENTIAL_DANGER, storm.level)
        assertEquals(RiskLevel.DANGER, hail.level)
    }

    @Test
    fun `alertas sao ordenados do maior para o menor risco`() {
        val alerts = evaluate(
            forecastOf(day(precipitation = 120.0, windSpeed = 45.0, uvIndex = 12.0))
        )

        assertEquals(RiskLevel.GREAT_DANGER, alerts.first().level)
        assertEquals(AlertType.HEAVY_RAIN, alerts.first().type)
        assertTrue(alerts.zipWithNext().all { (first, second) -> first.level >= second.level })
    }

    @Test
    fun `baixa umidade segue a escala do INMET`() {
        val alerts = evaluate(forecastOf(day(minHumidity = 18)))

        val dry = alerts.single { it.type == AlertType.LOW_HUMIDITY }
        assertEquals(RiskLevel.DANGER, dry.level)
    }

    @Test
    fun `codigo WMO e convertido na condicao correta`() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromWmoCode(0))
        assertEquals(WeatherCondition.HEAVY_RAIN, WeatherCondition.fromWmoCode(82))
        assertEquals(WeatherCondition.THUNDERSTORM_WITH_HAIL, WeatherCondition.fromWmoCode(99))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromWmoCode(null))
    }

    private fun forecastOf(vararg days: DailyForecast) = WeatherForecast(
        current = CurrentWeather(
            temperature = 25.0,
            apparentTemperature = 26.0,
            humidity = 60,
            precipitation = 0.0,
            windSpeed = 10.0,
            condition = WeatherCondition.CLEAR,
            isDay = true
        ),
        daily = days.toList()
    )

    private fun day(
        offset: Long = 0,
        condition: WeatherCondition = WeatherCondition.PARTLY_CLOUDY,
        maxTemperature: Double = 28.0,
        minTemperature: Double = 18.0,
        precipitation: Double = 0.0,
        windSpeed: Double = 15.0,
        uvIndex: Double = 7.0,
        minHumidity: Int = 55
    ) = DailyForecast(
        date = today.plusDays(offset),
        condition = condition,
        maxTemperature = maxTemperature,
        minTemperature = minTemperature,
        precipitationSum = precipitation,
        precipitationProbability = 30,
        maxWindSpeed = windSpeed,
        uvIndex = uvIndex,
        minHumidity = minHumidity
    )
}
