package com.nivelais.covidout.presentation.ui.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.OutReason
import com.nivelais.covidout.common.usecases.GeneratePdfUseCase
import com.nivelais.covidout.common.usecases.GetDefaultAttestationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * View model for the create attestation fragment
 */
class CreateAttestationViewModel(
    private val generatePdfUseCase: GeneratePdfUseCase,
    private val getDefaultAttestationUseCase: GetDefaultAttestationUseCase
) : ViewModel() {

    /*
    * Job and context for coroutines
    */
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    /**
     * Live data for the pdf file generated
     */
    val livePdfIdFile = MutableLiveData<Long>()

    /**
     * Live data that return the saved attestions infos
     */
    val liveSavedAttestation = MutableLiveData<AttestationEntity>()

    /**
     * List of the current picked reasons
     */
    val livePickedReasons: MutableLiveData<HashSet<OutReason>> = MutableLiveData(HashSet())

    init {
        //  Load the attestations from preferences
        getDefaultAttestationUseCase(scope, Unit) { result ->
            if (!result.isSuccess()) return@getDefaultAttestationUseCase

            // Send it to the view
            liveSavedAttestation.postValue(result.data)
        }
    }

    /**
     * Function called to generate the attestation
     */
    fun generateAttestation(
        name: String,
        surname: String,
        birthdate: String,
        birthplace: String,
        address: String,
        city: String,
        postalCode: String,
        outDate: String,
        outTime: String
    ) {
        // Generate the attestion from the input of the user
        val attestation = AttestationEntity(
            name,
            surname,
            birthdate,
            birthplace,
            address,
            city,
            postalCode,
            livePickedReasons.value?.toList() ?: ArrayList(),
            outDate,
            outTime
        )

        // Generate the PDf File
        generatePdfUseCase(scope, attestation) { result ->
            // Send it to the view if it was correctly generated
            if (result.isSuccess()) livePdfIdFile.postValue(result.data)
        }
    }

    /**
     * Save the picked reasons from the reasons dialog
     */
    fun savePickedReason(reason: OutReason, isPicked: Boolean) {
        livePickedReasons.value?.apply {
            // Update the list
            if (isPicked)
                add(reason)
            else
                remove(reason)
        }
        // Self reassign value to update observer
        livePickedReasons.postValue(livePickedReasons.value)
    }
}