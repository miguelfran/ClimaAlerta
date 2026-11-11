package br.com.fiap.climaalerta.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import br.com.fiap.climaalerta.R
import br.com.fiap.climaalerta.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OnboardingPage(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int
)

data class OnboardingUiState(
    val pages: List<OnboardingPage> = defaultOnboardingPages,
    val dontShowAgain: Boolean = false
)

private val defaultOnboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.img_onboarding_alerts,
        titleRes = R.string.onboarding_title_1,
        descriptionRes = R.string.onboarding_description_1
    ),
    OnboardingPage(
        imageRes = R.drawable.img_onboarding_location,
        titleRes = R.string.onboarding_title_2,
        descriptionRes = R.string.onboarding_description_2
    ),
    OnboardingPage(
        imageRes = R.drawable.img_onboarding_safety,
        titleRes = R.string.onboarding_title_3,
        descriptionRes = R.string.onboarding_description_3
    )
)

class OnboardingViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onDontShowAgainChange(checked: Boolean) {
        _uiState.update { it.copy(dontShowAgain = checked) }
    }

    fun completeOnboarding() {
        userPreferences.setOnboardingSeen(_uiState.value.dontShowAgain)
    }
}
