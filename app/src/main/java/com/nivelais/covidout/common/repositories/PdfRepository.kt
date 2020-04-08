package com.nivelais.covidout.common.repositories

import com.nivelais.covidout.common.entities.AttestationEntity
import java.io.File

interface PdfRepository {

    /**
     * Generate a PDF File from an attestation entity
     */
    suspend fun generate(attestation: AttestationEntity): File

}