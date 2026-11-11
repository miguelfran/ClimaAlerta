package br.com.fiap.climaalerta.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponseDto(
    @SerializedName("results") val results: List<GeocodingResultDto>?
)

data class GeocodingResultDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("feature_code") val featureCode: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("admin1") val state: String?,
    @SerializedName("population") val population: Long?
)
