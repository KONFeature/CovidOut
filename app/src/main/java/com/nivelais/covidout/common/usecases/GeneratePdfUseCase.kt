package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.repositories.PdfRepository
import java.io.File

/**
 * Generate a new PDF File from an attestation
 */
class GeneratePdfUseCase(private val pdfRepository: PdfRepository) : UseCase<Long, AttestationEntity>() {

    override suspend fun run(params: AttestationEntity): Data<Long> {
        // Generate the pdf file
        val pdfFile = pdfRepository.generate(params)

        // Return it's id
        return Data.succes(pdfFile)
    }

}