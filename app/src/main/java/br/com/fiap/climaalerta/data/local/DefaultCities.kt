package br.com.fiap.climaalerta.data.local

import br.com.fiap.climaalerta.domain.model.City

/**
 * Cidades monitoradas no primeiro uso, para o painel não iniciar vazio.
 * IDs e coordenadas iguais aos retornados pela Geocoding API da Open-Meteo.
 */
object DefaultCities {
    val cities = listOf(
        City(3448439, "São Paulo", "São Paulo", "Brasil", -23.5475, -46.63611),
        City(3451190, "Rio de Janeiro", "Rio de Janeiro", "Brasil", -22.90642, -43.18223),
        City(3452925, "Porto Alegre", "Rio Grande do Sul", "Brasil", -30.03283, -51.23019),
        City(3390760, "Recife", "Pernambuco", "Brasil", -8.05389, -34.88111),
        City(3465038, "Cuiabá", "Mato Grosso", "Brasil", -15.59611, -56.09667)
    )
}
