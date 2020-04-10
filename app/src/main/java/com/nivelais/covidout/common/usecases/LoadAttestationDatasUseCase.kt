package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.repositories.PreferencesRepository

/**
 * Generate a new PDF File from an attestation
 */
class LoadAttestationDatasUseCase(private val preferencesRepository: PreferencesRepository) : UseCase<AttestationEntity, Unit>() {

    override suspend fun run(params: Unit): Data<AttestationEntity> {
        return Data.succes(preferencesRepository.getSavedAttestation())
    }

}