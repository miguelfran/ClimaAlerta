package br.com.fiap.climaalerta.domain.model

enum class WeatherCondition(val description: String) {
    CLEAR("Céu limpo"),
    PARTLY_CLOUDY("Parcialmente nublado"),
    CLOUDY("Nublado"),
    FOG("Neblina"),
    DRIZZLE("Garoa"),
    RAIN("Chuva"),
    HEAVY_RAIN("Chuva forte"),
    SNOW("Neve"),
    THUNDERSTORM("Tempestade"),
    THUNDERSTORM_WITH_HAIL("Tempestade com granizo"),
    UNKNOWN("Indefinido");

    companion object {
        fun fromWmoCode(code: Int?): WeatherCondition = when (code) {
            0 -> CLEAR
            1, 2 -> PARTLY_CLOUDY
            3 -> CLOUDY
            45, 48 -> FOG
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 63, 66, 80, 81 -> RAIN
            65, 67, 82 -> HEAVY_RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            95 -> THUNDERSTORM
            96, 99 -> THUNDERSTORM_WITH_HAIL
            else -> UNKNOWN
        }
    }
}
