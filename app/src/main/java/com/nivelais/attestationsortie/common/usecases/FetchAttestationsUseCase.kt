package com.nivelais.attestationsortie.common.usecases

import com.nivelais.attestationsortie.common.entities.AttestationPdfEntity
import com.nivelais.attestationsortie.common.repositories.PdfRepository

/**
 * Generate a new PDF File from an attestation
 */
class FetchAttestationsUseCase(private val pdfRepository: PdfRepository) :
    UseCase<List<AttestationPdfEntity>, Unit>() {

    override suspend fun run(params: Unit): Data<List<AttestationPdfEntity>> {
        // Generate the pdf file
        val pdfFiles = pdfRepository.getAttestations()

        // Return it's id
        return Data.succes(pdfFiles)
    }

}