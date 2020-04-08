package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.Data
import com.nivelais.covidout.common.repositories.PdfRepository
import java.io.File

/**
 * Generate a new PDF File from an attestation
 */
class GeneratePdfUseCase(private val pdfRepository: PdfRepository) : UseCase<File, AttestationEntity>() {

    override suspend fun run(params: AttestationEntity): Data<File> {
        val pdfFile = pdfRepository.generate(params)
        return Data.succes(pdfFile)
    }

}