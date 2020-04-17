package com.nivelais.attestationsortie.common.repositories

import com.nivelais.attestationsortie.common.entities.PersonnalInformationEntity

interface PreferencesRepository {

    /**
     * Save data from an attestation to the preferences
     */
    suspend fun savePersonalInformation(information: PersonnalInformationEntity)

    /**
     * Get the saved information for attestation from preferences
     */
    suspend fun getPersonalInformation() : PersonnalInformationEntity

}