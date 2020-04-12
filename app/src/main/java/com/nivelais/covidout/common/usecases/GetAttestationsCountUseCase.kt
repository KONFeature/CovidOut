package com.nivelais.covidout.common.usecases

import com.nivelais.covidout.common.repositories.PdfRepository
import java.util.*

/**
 * Get the number of valid attestations and total number of attestations
 */
class GetAttestationsCountUseCase(
    private val pdfRepository: PdfRepository
) : UseCase<Pair<Int, Int>, Unit>() {

    override suspend fun run(params: Unit): Data<Pair<Int, Int>> {
        var validAttestationsCount = 0
        val allAttestations = pdfRepository.getAttestations()

        allAttestations.forEach { attestation ->
            // Get the start and the end of validity of the attestations
            val startValidity = attestation.outDateTime.time
            val endValidity = Calendar.getInstance().apply {
                time = attestation.outDateTime
                add(Calendar.HOUR, 1)
            }.timeInMillis

            // Check if we are in the current time range
            if (System.currentTimeMillis() in startValidity until endValidity) validAttestationsCount++
        }

        return Data.succes(Pair(validAttestationsCount, allAttestations.size))
    }
}