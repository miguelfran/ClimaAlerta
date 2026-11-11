package br.com.fiap.climaalerta.data.local

import android.content.Context
import androidx.core.content.edit

class UserPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun hasSeenOnboarding(): Boolean = preferences.getBoolean(KEY_ONBOARDING_SEEN, false)

    fun setOnboardingSeen(seen: Boolean) {
        preferences.edit { putBoolean(KEY_ONBOARDING_SEEN, seen) }
    }

    private companion object {
        const val PREFERENCES_NAME = "clima_alerta_preferences"
        const val KEY_ONBOARDING_SEEN = "onboarding_seen"
    }
}
