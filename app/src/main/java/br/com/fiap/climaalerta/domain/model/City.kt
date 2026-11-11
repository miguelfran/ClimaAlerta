package br.com.fiap.climaalerta.domain.model

/**
 * Cidade monitorada pelo usuário ou retornada pela busca (Geocoding API).
 */
data class City(
    val id: Long,
    val name: String,
    val state: String?,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val population: Long? = null
) {
    /** Nome amigável para exibição. Ex.: "Recife, Pernambuco". */
    val displayName: String
        get() = if (state.isNullOrBlank()) name else "$name, $state"
}
