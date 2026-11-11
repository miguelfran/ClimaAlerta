package br.com.fiap.climaalerta.domain.model

data class City(
    val id: Long,
    val name: String,
    val state: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val population: Long? = null
) {
    val displayName: String
        get() = if (state.isNullOrBlank()) name else "$name, $state"
}
