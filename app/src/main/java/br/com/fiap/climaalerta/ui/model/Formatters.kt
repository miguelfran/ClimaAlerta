package br.com.fiap.climaalerta.ui.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

/*
 * Funções de formatação usadas apenas para apresentação.
 */

private val PT_BR: Locale = Locale.forLanguageTag("pt-BR")
private val DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd/MM", PT_BR)
private val SHORT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM", PT_BR)
private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", PT_BR)

/** 23.6 -> "24°" */
fun Double.toTemperatureLabel(): String = "${roundToInt()}°"

fun Double.toWindLabel(): String = "${roundToInt()} km/h"

fun Double.toPrecipitationLabel(): String = "${roundToInt()} mm"

fun Int.toPercentLabel(): String = "$this%"

/** "Hoje", "Amanhã" ou "qui., 13/11". */
fun LocalDate.toDayLabel(today: LocalDate = LocalDate.now()): String = when (this) {
    today -> "Hoje"
    today.plusDays(1) -> "Amanhã"
    else -> format(DAY_FORMATTER).replaceFirstChar { it.titlecase(PT_BR) }
}

/**
 * Período de um alerta:
 * - um único dia: "Hoje", "Amanhã" ou "Sex., 13/11";
 * - dias seguidos: "13/11 a 15/11";
 * - dias alternados: "3 dias entre 11/11 e 17/11".
 */
fun formatPeriod(
    start: LocalDate,
    end: LocalDate,
    activeDays: Int,
    today: LocalDate = LocalDate.now()
): String {
    if (start == end) return start.toDayLabel(today)
    val startLabel = start.format(SHORT_DATE_FORMATTER)
    val endLabel = end.format(SHORT_DATE_FORMATTER)
    val periodLength = ChronoUnit.DAYS.between(start, end) + 1
    return if (activeDays >= periodLength) {
        "$startLabel a $endLabel"
    } else {
        "$activeDays dias entre $startLabel e $endLabel"
    }
}

/** Horário da última atualização: "18:45". */
fun Long.toTimeLabel(): String =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime().format(TIME_FORMATTER)

/** 1653461 -> "1.653.461" */
fun Long.toPopulationLabel(): String = String.format(PT_BR, "%,d", this)
