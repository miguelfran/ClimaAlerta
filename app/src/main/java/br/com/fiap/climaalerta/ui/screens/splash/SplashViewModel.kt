package br.com.fiap.climaalerta.ui.screens.splash

import androidx.lifecycle.ViewModel
import br.com.fiap.climaalerta.data.local.UserPreferences

enum class SplashDestination { ONBOARDING, HOME }

/**
 * Decide para onde a Splash deve seguir consultando o SharedPreferences:
 * se o usuário já marcou que viu a introdução, vai direto ao Painel.
 */
class SplashViewModel(userPreferences: UserPreferences) : ViewModel() {

    val destination: SplashDestination =
        if (userPreferences.hasSeenOnboarding()) SplashDestination.HOME else SplashDestination.ONBOARDING

    companion object {
        /** Tempo mínimo de exibição do logo, contado a partir do primeiro frame da Splash. */
        const val SPLASH_DURATION_MILLIS = 2_000L
    }
}
