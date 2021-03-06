package com.nivelais.attestationsortie.common.usecases

import com.nivelais.attestationsortie.common.entities.AttestationEntity
import com.nivelais.attestationsortie.common.entities.PersonnalInformationEntity
import com.nivelais.attestationsortie.common.repositories.PdfRepository
import com.nivelais.attestationsortie.common.repositories.PreferencesRepository

/**
 * Generate a new PDF File from an attestation
 */
class GeneratePdfUseCase(
    private val pdfRepository: PdfRepository,
    private val preferencesRepository: PreferencesRepository
) : UseCase<Long, AttestationEntity>() {

    override suspend fun run(params: AttestationEntity): Data<Long> {
        // Save personal data to preference
        preferencesRepository.savePersonalInformation(PersonnalInformationEntity(
            name = params.name,
            surname = params.surname,
            birthDate = params.birthDate,
            birthPlace = params.birthPlace,
            address = params.address,
            city = params.city,
            postalCode = params.postalCode
        ))

        // Generate the pdf file
        val pdfFile = pdfRepository.generate(params)

        // Return it's id
        return Data.succes(pdfFile)
    }

}