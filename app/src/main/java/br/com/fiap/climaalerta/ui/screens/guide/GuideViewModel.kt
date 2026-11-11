package br.com.fiap.climaalerta.ui.screens.guide

import androidx.lifecycle.ViewModel
import br.com.fiap.climaalerta.data.local.UserPreferences
import br.com.fiap.climaalerta.data.repository.SafetyGuideRepository
import br.com.fiap.climaalerta.domain.model.EmergencyContact
import br.com.fiap.climaalerta.domain.model.SafetyTopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GuideUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val topics: List<SafetyTopic> = emptyList(),
    val showOnboardingOnStart: Boolean = false
)

class GuideViewModel(
    private val userPreferences: UserPreferences,
    safetyGuideRepository: SafetyGuideRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GuideUiState(
            contacts = safetyGuideRepository.getEmergencyContacts(),
            topics = safetyGuideRepository.getSafetyTopics(),
            showOnboardingOnStart = !userPreferences.hasSeenOnboarding()
        )
    )
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    fun onShowOnboardingChange(show: Boolean) {
        userPreferences.setOnboardingSeen(!show)
        _uiState.update { it.copy(showOnboardingOnStart = show) }
    }
}
