package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.common.repositories.PdfRepository

/**
 * Generate a new PDF File from an attestation
 */
class GetAttestationPdfUseCase(private val pdfRepository: PdfRepository) :
    UseCase<AttestationPdfEntity, Long>() {

    override suspend fun run(params: Long): Data<AttestationPdfEntity> {
        // Generate the pdf file
        val pdfFile = pdfRepository.getAttestation(params)

        return Data.succes(pdfFile)
    }

}