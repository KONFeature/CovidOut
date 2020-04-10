package com.nivelais.covidout.presentation.ui.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.OutReason
import com.nivelais.covidout.common.usecases.GeneratePdfUseCase
import com.nivelais.covidout.common.usecases.LoadAttestationDatasUseCase
import com.nivelais.covidout.common.usecases.SaveAttestationDatasUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * View model for the create attestation fragment
 */
class CreateAttestationViewModel(
    private val generatePdfUseCase: GeneratePdfUseCase,
    private val loadAttestationDatasUseCase: LoadAttestationDatasUseCase,
    private val saveAttestationDatasUseCase: SaveAttestationDatasUseCase
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

    init {
        //  Load the attestations from preferences
        loadAttestationDatasUseCase(scope, Unit) { result ->
            if (!result.isSuccess()) return@loadAttestationDatasUseCase

            // Attestations from preferences
            val attestationDatas = result.data!!

            // Date format for the outDate and outTime
            val outDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val outTimeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

            // Put current date and time
            attestationDatas.outDate = outDateFormat.format(Date())
            attestationDatas.outTime = outTimeFormat.format(Date())

            // Send it to the view
            liveSavedAttestation.postValue(attestationDatas)
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
        outReason: OutReason,
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
            outReason,
            outDate,
            outTime
        )

        // Backup this infos in preferences
        saveAttestationDatasUseCase(scope, attestation)

        // Generate the PDf File
        generatePdfUseCase(scope, attestation) { result ->
            // Send it to the view if it was correctly generated
            if (result.isSuccess()) livePdfIdFile.postValue(result.data)
        }
    }
}