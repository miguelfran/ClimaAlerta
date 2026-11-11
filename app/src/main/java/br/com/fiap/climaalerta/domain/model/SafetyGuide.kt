package br.com.fiap.climaalerta.domain.model

/** Telefone de emergência exibido na tela de orientações. */
data class EmergencyContact(
    val name: String,
    val phoneNumber: String,
    val description: String
)

/** Conjunto de orientações de segurança para um tipo de evento climático. */
data class SafetyTopic(
    val type: AlertType,
    val title: String,
    val tips: List<String>
)
