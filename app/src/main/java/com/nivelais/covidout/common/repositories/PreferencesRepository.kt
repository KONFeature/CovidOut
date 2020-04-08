package com.nivelais.covidout.common.repositories

import com.nivelais.covidout.common.entities.AttestationEntity

interface PreferencesRepository {

    /**
     * Save data from an attestation to the preferences
     */
    suspend fun saveAttestationData(attestation: AttestationEntity)

    /**
     * Get the saved attestation from preferences
     */
    suspend fun getSavedAttestation() : AttestationEntity

}