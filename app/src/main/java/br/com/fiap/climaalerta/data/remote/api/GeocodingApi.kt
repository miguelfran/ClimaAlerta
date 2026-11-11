package br.com.fiap.climaalerta.data.remote.api

import br.com.fiap.climaalerta.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = MAX_RESULTS,
        @Query("language") language: String = "pt",
        @Query("countryCode") countryCode: String = "BR",
        @Query("format") format: String = "json"
    ): GeocodingResponseDto

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/"
        private const val MAX_RESULTS = 20
    }
}
