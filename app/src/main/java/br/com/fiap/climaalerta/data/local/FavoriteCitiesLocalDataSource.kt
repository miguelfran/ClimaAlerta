package br.com.fiap.climaalerta.data.local

import android.content.Context
import androidx.core.content.edit
import br.com.fiap.climaalerta.domain.model.City
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken

class FavoriteCitiesLocalDataSource(
    context: Context,
    private val gson: Gson = Gson()
) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val listType = object : TypeToken<List<StoredCity>>() {}.type

    fun load(): List<City>? {
        val json = preferences.getString(KEY_FAVORITE_CITIES, null) ?: return null
        return try {
            gson.fromJson<List<StoredCity>>(json, listType).map { it.toDomain() }
        } catch (exception: JsonParseException) {
            null
        }
    }

    fun save(cities: List<City>) {
        val json = gson.toJson(cities.map { StoredCity.fromDomain(it) })
        preferences.edit { putString(KEY_FAVORITE_CITIES, json) }
    }

    private data class StoredCity(
        val id: Long,
        val name: String,
        val state: String?,
        val country: String?,
        val latitude: Double,
        val longitude: Double
    ) {
        fun toDomain() = City(id, name, state, country, latitude, longitude)

        companion object {
            fun fromDomain(city: City) = StoredCity(
                id = city.id,
                name = city.name,
                state = city.state,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude
            )
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "clima_alerta_favorites"
        const val KEY_FAVORITE_CITIES = "favorite_cities"
    }
}
