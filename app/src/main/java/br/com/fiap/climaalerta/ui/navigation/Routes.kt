package br.com.fiap.climaalerta.ui.navigation

import br.com.fiap.climaalerta.domain.model.City
import kotlinx.serialization.Serializable

/*
 * Rotas do Navigation Compose com segurança de tipos (kotlinx.serialization).
 */

@Serializable
data object SplashRoute

@Serializable
data object OnboardingRoute

@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data object AlertsRoute

@Serializable
data object GuideRoute

/** Detalhe de uma cidade: recebe os dados necessários para consultar a previsão. */
@Serializable
data class CityDetailRoute(
    val cityId: Long,
    val name: String,
    val state: String? = null,
    val country: String? = null,
    val latitude: Double,
    val longitude: Double
) {
    fun toCity() = City(
        id = cityId,
        name = name,
        state = state,
        country = country,
        latitude = latitude,
        longitude = longitude
    )

    companion object {
        fun from(city: City) = CityDetailRoute(
            cityId = city.id,
            name = city.name,
            state = city.state,
            country = city.country,
            latitude = city.latitude,
            longitude = city.longitude
        )
    }
}
