package br.com.fiap.climaalerta.domain.model

data class EmergencyContact(
    val name: String,
    val phoneNumber: String,
    val description: String
)

data class SafetyTopic(
    val type: AlertType,
    val title: String,
    val tips: List<String>
)
