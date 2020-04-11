package com.nivelais.covidout.common.repositories

import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.AttestationPdfEntity

interface PdfRepository {

    /**
     * Generate a PDF File from an attestation entity
     */
    suspend fun generate(attestation: AttestationEntity): Long

    /**
     * Retreive an attestation pdf from it's id
     */
    suspend fun getAttestation(id: Long): AttestationPdfEntity

    /**
     * Retreive all the attestations pdf
     */
    suspend fun getAttestations(): List<AttestationPdfEntity>

    /**
     * Delete an attestation pdf from it's id
     */
    suspend fun deleteAttestation(id: Long)
}