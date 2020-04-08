package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.Data
import com.nivelais.covidout.common.repositories.PdfRepository
import com.nivelais.covidout.common.repositories.PreferencesRepository
import java.io.File

/**
 * Generate a new PDF File from an attestation
 */
class SaveAttestationDatasUseCase(private val preferencesRepository: PreferencesRepository) : UseCase<Unit, AttestationEntity>() {

    override suspend fun run(params: AttestationEntity): Data<Unit> {
        preferencesRepository.saveAttestationData(params)
        return Data.succes(Unit)
    }

}