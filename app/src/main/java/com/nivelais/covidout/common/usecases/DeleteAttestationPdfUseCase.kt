package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.common.repositories.PdfRepository
import java.io.File

/**
 * Generate a new PDF File from an attestation
 */
class DeleteAttestationPdfUseCase(private val pdfRepository: PdfRepository) : UseCase<Unit, Long>() {

    override suspend fun run(params: Long): Data<Unit> {
        // Delete the pdf file
        pdfRepository.deleteAttestation(params)
        return Data.succes(Unit)
    }

}