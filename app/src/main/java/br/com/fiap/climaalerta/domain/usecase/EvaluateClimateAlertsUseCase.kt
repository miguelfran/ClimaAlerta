package br.com.fiap.climaalerta.domain.usecase

import br.com.fiap.climaalerta.domain.model.AlertType
import br.com.fiap.climaalerta.domain.model.ClimateAlert
import br.com.fiap.climaalerta.domain.model.DailyForecast
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.domain.model.WeatherCondition
import br.com.fiap.climaalerta.domain.model.WeatherForecast
import java.time.LocalDate
import java.util.Locale

/**
 * Regra de negócio central do app: transforma a previsão diária recebida do WebService
 * em alertas climáticos para o cidadão.
 *
 * Os limiares foram simplificados a partir dos critérios de aviso do INMET
 * (perigo potencial, perigo e grande perigo). Quando o mesmo evento ocorre em vários dias,
 * ele é consolidado em um único alerta com o pior nível e o valor de pico do período.
 */
class EvaluateClimateAlertsUseCase {

    operator fun invoke(forecast: WeatherForecast): List<ClimateAlert> =
        forecast.daily
            .flatMap(::evaluateDay)
            .groupBy { it.type }
            .map { (type, occurrences) -> consolidate(type, occurrences) }
            .sortedWith(compareByDescending<ClimateAlert> { it.level }.thenBy { it.startDate })

    /** Ocorrência de um evento em um único dia, antes da consolidação. */
    private data class Occurrence(
        val type: AlertType,
        val level: RiskLevel,
        val date: LocalDate,
        val value: Double
    )

    private fun evaluateDay(day: DailyForecast): List<Occurrence> = buildList {
        rainLevel(day.precipitationSum)?.let {
            add(Occurrence(AlertType.HEAVY_RAIN, it, day.date, day.precipitationSum))
        }
        stormLevel(day.condition)?.let {
            add(Occurrence(AlertType.STORM, it, day.date, day.maxWindSpeed))
        }
        heatLevel(day.maxTemperature)?.let {
            add(Occurrence(AlertType.EXTREME_HEAT, it, day.date, day.maxTemperature))
        }
        coldLevel(day.minTemperature)?.let {
            add(Occurrence(AlertType.INTENSE_COLD, it, day.date, day.minTemperature))
        }
        windLevel(day.maxWindSpeed)?.let {
            add(Occurrence(AlertType.STRONG_WIND, it, day.date, day.maxWindSpeed))
        }
        uvLevel(day.uvIndex)?.let {
            add(Occurrence(AlertType.EXTREME_UV, it, day.date, day.uvIndex))
        }
        humidityLevel(day.minHumidity)?.let {
            add(Occurrence(AlertType.LOW_HUMIDITY, it, day.date, day.minHumidity.toDouble()))
        }
    }

    private fun consolidate(type: AlertType, occurrences: List<Occurrence>): ClimateAlert {
        // Para frio e umidade, o "pior" valor é o menor; para os demais, o maior.
        val lowerIsWorse = type == AlertType.INTENSE_COLD || type == AlertType.LOW_HUMIDITY
        val peak = if (lowerIsWorse) occurrences.minOf { it.value } else occurrences.maxOf { it.value }
        val level = occurrences.maxOf { it.level }
        return ClimateAlert(
            type = type,
            level = level,
            startDate = occurrences.minOf { it.date },
            endDate = occurrences.maxOf { it.date },
            activeDays = occurrences.map { it.date }.distinct().size,
            peakValue = peak,
            description = describe(type, level, peak)
        )
    }

    private fun describe(type: AlertType, level: RiskLevel, peak: Double): String = when (type) {
        AlertType.HEAVY_RAIN -> format("Até %.0f mm de chuva previstos em um único dia.", peak)
        AlertType.STORM -> if (level >= RiskLevel.DANGER) {
            "Tempestade com possibilidade de granizo, raios e rajadas de vento."
        } else {
            "Tempestade com raios e rajadas de vento."
        }
        AlertType.EXTREME_HEAT -> format("Temperatura máxima de até %.0f °C.", peak)
        AlertType.INTENSE_COLD -> format("Temperatura mínima de até %.0f °C.", peak)
        AlertType.STRONG_WIND -> format("Ventos de até %.0f km/h.", peak)
        AlertType.EXTREME_UV -> format("Índice UV de até %.0f (categoria extrema).", peak)
        AlertType.LOW_HUMIDITY -> format("Umidade relativa do ar de até %.0f%%.", peak)
    }

    private fun format(pattern: String, value: Double) = String.format(PT_BR, pattern, value)

    private fun rainLevel(mm: Double): RiskLevel? = when {
        mm >= RAIN_GREAT_DANGER_MM -> RiskLevel.GREAT_DANGER
        mm >= RAIN_DANGER_MM -> RiskLevel.DANGER
        mm >= RAIN_POTENTIAL_MM -> RiskLevel.POTENTIAL_DANGER
        else -> null
    }

    private fun stormLevel(condition: WeatherCondition): RiskLevel? = when (condition) {
        WeatherCondition.THUNDERSTORM_WITH_HAIL -> RiskLevel.DANGER
        WeatherCondition.THUNDERSTORM -> RiskLevel.POTENTIAL_DANGER
        else -> null
    }

    private fun heatLevel(celsius: Double): RiskLevel? = when {
        celsius >= HEAT_GREAT_DANGER_C -> RiskLevel.GREAT_DANGER
        celsius >= HEAT_DANGER_C -> RiskLevel.DANGER
        celsius >= HEAT_POTENTIAL_C -> RiskLevel.POTENTIAL_DANGER
        else -> null
    }

    private fun coldLevel(celsius: Double): RiskLevel? = when {
        celsius <= COLD_GREAT_DANGER_C -> RiskLevel.GREAT_DANGER
        celsius <= COLD_DANGER_C -> RiskLevel.DANGER
        celsius <= COLD_POTENTIAL_C -> RiskLevel.POTENTIAL_DANGER
        else -> null
    }

    private fun windLevel(kmh: Double): RiskLevel? = when {
        kmh >= WIND_GREAT_DANGER_KMH -> RiskLevel.GREAT_DANGER
        kmh >= WIND_DANGER_KMH -> RiskLevel.DANGER
        kmh >= WIND_POTENTIAL_KMH -> RiskLevel.POTENTIAL_DANGER
        else -> null
    }

    private fun uvLevel(index: Double): RiskLevel? =
        if (index >= UV_EXTREME_INDEX) RiskLevel.POTENTIAL_DANGER else null

    private fun humidityLevel(percent: Int): RiskLevel? = when {
        percent <= HUMIDITY_GREAT_DANGER -> RiskLevel.GREAT_DANGER
        percent <= HUMIDITY_DANGER -> RiskLevel.DANGER
        percent <= HUMIDITY_POTENTIAL -> RiskLevel.POTENTIAL_DANGER
        else -> null
    }

    companion object {
        private val PT_BR: Locale = Locale.forLanguageTag("pt-BR")

        const val RAIN_POTENTIAL_MM = 20.0
        const val RAIN_DANGER_MM = 50.0
        const val RAIN_GREAT_DANGER_MM = 100.0

        const val HEAT_POTENTIAL_C = 35.0
        const val HEAT_DANGER_C = 38.0
        const val HEAT_GREAT_DANGER_C = 40.0

        const val COLD_POTENTIAL_C = 5.0
        const val COLD_DANGER_C = 2.0
        const val COLD_GREAT_DANGER_C = -2.0

        const val WIND_POTENTIAL_KMH = 40.0
        const val WIND_DANGER_KMH = 60.0
        const val WIND_GREAT_DANGER_KMH = 100.0

        const val UV_EXTREME_INDEX = 11.0

        const val HUMIDITY_POTENTIAL = 30
        const val HUMIDITY_DANGER = 20
        const val HUMIDITY_GREAT_DANGER = 12
    }
}
