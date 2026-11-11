package br.com.fiap.climaalerta.ui.screens.splash

import androidx.lifecycle.ViewModel
import br.com.fiap.climaalerta.data.local.UserPreferences

enum class SplashDestination { ONBOARDING, HOME }

class SplashViewModel(userPreferences: UserPreferences) : ViewModel() {

    val destination: SplashDestination =
        if (userPreferences.hasSeenOnboarding()) SplashDestination.HOME else SplashDestination.ONBOARDING

    companion object {
        const val SPLASH_DURATION_MILLIS = 2_000L
    }
}
