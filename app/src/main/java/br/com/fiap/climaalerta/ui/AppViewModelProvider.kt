package br.com.fiap.climaalerta.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.climaalerta.ClimaAlertaApplication
import br.com.fiap.climaalerta.ui.screens.alerts.AlertsViewModel
import br.com.fiap.climaalerta.ui.screens.detail.CityDetailViewModel
import br.com.fiap.climaalerta.ui.screens.guide.GuideViewModel
import br.com.fiap.climaalerta.ui.screens.home.HomeViewModel
import br.com.fiap.climaalerta.ui.screens.onboarding.OnboardingViewModel
import br.com.fiap.climaalerta.ui.screens.search.SearchViewModel
import br.com.fiap.climaalerta.ui.screens.splash.SplashViewModel

/**
 * Fábrica única de ViewModels: injeta as dependências do [br.com.fiap.climaalerta.di.AppContainer].
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            SplashViewModel(climaAlertaApplication().container.userPreferences)
        }
        initializer {
            OnboardingViewModel(climaAlertaApplication().container.userPreferences)
        }
        initializer {
            val container = climaAlertaApplication().container
            HomeViewModel(container.favoritesRepository, container.weatherRepository)
        }
        initializer {
            val container = climaAlertaApplication().container
            SearchViewModel(container.cityRepository, container.favoritesRepository)
        }
        initializer {
            val container = climaAlertaApplication().container
            CityDetailViewModel(
                savedStateHandle = createSavedStateHandle(),
                weatherRepository = container.weatherRepository,
                favoritesRepository = container.favoritesRepository
            )
        }
        initializer {
            val container = climaAlertaApplication().container
            AlertsViewModel(container.favoritesRepository, container.weatherRepository)
        }
        initializer {
            val container = climaAlertaApplication().container
            GuideViewModel(container.userPreferences, container.safetyGuideRepository)
        }
    }
}

private fun CreationExtras.climaAlertaApplication(): ClimaAlertaApplication =
    this[APPLICATION_KEY] as ClimaAlertaApplication
