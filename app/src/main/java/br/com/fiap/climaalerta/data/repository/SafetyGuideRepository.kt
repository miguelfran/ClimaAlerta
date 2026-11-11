package br.com.fiap.climaalerta.data.repository

import br.com.fiap.climaalerta.data.local.SafetyGuideLocalDataSource
import br.com.fiap.climaalerta.domain.model.EmergencyContact
import br.com.fiap.climaalerta.domain.model.SafetyTopic

interface SafetyGuideRepository {
    fun getEmergencyContacts(): List<EmergencyContact>
    fun getSafetyTopics(): List<SafetyTopic>
}

class SafetyGuideRepositoryImpl(
    private val localDataSource: SafetyGuideLocalDataSource
) : SafetyGuideRepository {
    override fun getEmergencyContacts() = localDataSource.getEmergencyContacts()
    override fun getSafetyTopics() = localDataSource.getSafetyTopics()
}
