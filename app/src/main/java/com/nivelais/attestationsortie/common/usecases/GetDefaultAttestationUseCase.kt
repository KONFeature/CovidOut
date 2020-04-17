package com.nivelais.attestationsortie.common.usecases

import com.nivelais.attestationsortie.common.entities.AttestationEntity
import com.nivelais.attestationsortie.common.repositories.PreferencesRepository
import com.nivelais.attestationsortie.common.utils.DateUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Get a default attestations from preferences data
 */
class GetDefaultAttestationUseCase(
    private val preferencesRepository: PreferencesRepository
) : UseCase<AttestationEntity, Unit>() {

    override suspend fun run(params: Unit): Data<AttestationEntity> {
        // Retrieve personal data from preference
        val personalInfo = preferencesRepository.getPersonalInformation()

        // Get currentDate
        val currentDate = Date()

        // Return it's id
        return Data.succes(
            AttestationEntity(
                name = personalInfo.name,
                surname = personalInfo.surname,
                birthDate = personalInfo.birthDate,
                birthPlace = personalInfo.birthPlace,
                address = personalInfo.address,
                city = personalInfo.city,
                postalCode = personalInfo.postalCode,
                outReasons = ArrayList(),
                outDate = DateUtils.dateFormat.format(currentDate),
                outTime = DateUtils.timeFormat.format(currentDate)
            )
        )
    }

}