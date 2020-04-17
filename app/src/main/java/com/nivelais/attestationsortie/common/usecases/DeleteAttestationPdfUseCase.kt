package com.nivelais.attestationsortie.common.usecases

import com.nivelais.attestationsortie.common.repositories.PdfRepository

/**
 * Generate a new PDF File from an attestation
 */
class DeleteAttestationPdfUseCase(private val pdfRepository: PdfRepository) :
    UseCase<Unit, Long>() {

    override suspend fun run(params: Long): Data<Unit> {
        // Delete the pdf file
        pdfRepository.deleteAttestation(params)
        return Data.succes(Unit)
    }

}