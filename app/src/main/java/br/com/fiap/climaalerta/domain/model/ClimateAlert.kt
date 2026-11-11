package br.com.fiap.climaalerta.domain.model

import java.time.LocalDate

/**
 * Escala de risco inspirada nos avisos meteorológicos do INMET.
 * A ordem dos itens importa: quanto maior o ordinal, maior o risco.
 */
enum class RiskLevel(val label: String) {
    NONE("Sem alertas"),
    POTENTIAL_DANGER("Perigo potencial"),
    DANGER("Perigo"),
    GREAT_DANGER("Grande perigo")
}

/** Tipos de evento climático monitorados e a orientação básica para cada um. */
enum class AlertType(val label: String, val recommendation: String) {
    HEAVY_RAIN(
        label = "Chuvas intensas",
        recommendation = "Evite áreas alagáveis e encostas, não atravesse ruas inundadas e desligue " +
            "os aparelhos elétricos se a água entrar em casa."
    ),
    STORM(
        label = "Tempestade",
        recommendation = "Procure abrigo em local fechado, afaste-se de árvores, postes e estruturas " +
            "metálicas e evite usar aparelhos ligados à tomada."
    ),
    EXTREME_HEAT(
        label = "Calor extremo",
        recommendation = "Beba água com frequência, evite o sol entre 10h e 16h e dê atenção especial " +
            "a idosos, crianças e animais."
    ),
    INTENSE_COLD(
        label = "Frio intenso",
        recommendation = "Agasalhe-se em camadas, proteja crianças e idosos e indique abrigos públicos " +
            "para pessoas em situação de rua."
    ),
    STRONG_WIND(
        label = "Ventos fortes",
        recommendation = "Não se abrigue sob árvores, recolha objetos soltos de varandas e quintais e " +
            "evite circular perto de placas e fiações."
    ),
    EXTREME_UV(
        label = "Radiação UV extrema",
        recommendation = "Use protetor solar, chapéu e óculos escuros e evite o sol forte no meio do dia."
    ),
    LOW_HUMIDITY(
        label = "Baixa umidade",
        recommendation = "Hidrate-se, evite atividades físicas ao ar livre nas horas mais secas e " +
            "umidifique os ambientes."
    )
}

/**
 * Alerta consolidado de um tipo de evento para uma cidade, cobrindo o período em que ele ocorre.
 */
data class ClimateAlert(
    val type: AlertType,
    val level: RiskLevel,
    val startDate: LocalDate,
    val endDate: LocalDate,
    /** Quantidade de dias do período em que o evento está previsto. */
    val activeDays: Int,
    val peakValue: Double,
    val description: String
)

/** Alerta associado à cidade de origem (usado na central de alertas). */
data class CityAlert(
    val city: City,
    val alert: ClimateAlert
)
