package com.nivelais.attestationsortie.presentation.ui.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.attestationsortie.common.entities.AttestationEntity
import com.nivelais.attestationsortie.common.entities.OutReason
import com.nivelais.attestationsortie.common.usecases.GeneratePdfUseCase
import com.nivelais.attestationsortie.common.usecases.GetDefaultAttestationUseCase
import com.nivelais.attestationsortie.common.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
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
    val liveGeneratedPdfId = MutableLiveData<Long>()

    /**
     * Live data that return the saved attestions infos
     */
    val liveSavedAttestation = MutableLiveData<AttestationEntity>()

    /**
     * Live data of the list of the current picked reasons
     */
    val livePickedReasons = MutableLiveData(HashSet<OutReason>())

    /**
     * Live data for the field validation
     */
    val liveFormValidation = MutableLiveData(EnumMap<FormField, Boolean>(FormField::class.java))

    init {
        //  Load the attestations from preferences
        getDefaultAttestationUseCase(scope, Unit) { result ->
            if (result.isSuccess())
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
        // Firslty we check the validity of the form
        val isFormValid = isFormValid(
            name,
            surname,
            birthdate,
            birthplace,
            address,
            city,
            postalCode,
            outDate,
            outTime
        )
        if (!isFormValid) return // Exit if the form isn't valid

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
            if (result.isSuccess()) liveGeneratedPdfId.postValue(result.data)
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

    /**
     * Validate our creation form
     */
    private fun isFormValid(
        name: String,
        surname: String,
        birthdate: String,
        birthplace: String,
        address: String,
        city: String,
        postalCode: String,
        outDate: String,
        outTime: String
    ): Boolean {
        // Check the state of each input field
        val formState = EnumMap<FormField, Boolean>(FormField::class.java)
        formState[FormField.NAME] = name.isNotEmpty()
        formState[FormField.SURNAME] = surname.isNotEmpty()
        formState[FormField.BIRTHDATE] = DateUtils.isValidDate(birthdate)
        formState[FormField.BIRTHPLACE] = birthplace.isNotEmpty()
        formState[FormField.ADDRESS] = address.isNotEmpty()
        formState[FormField.CITY] = city.isNotEmpty()
        formState[FormField.POSTAL_CODE] = postalCode.isNotEmpty()
        formState[FormField.OUT_DATE] = DateUtils.isValidDate(outDate)
        formState[FormField.OUT_TIME] = DateUtils.isValidTime(outTime)

        // Check the user picked at least on out reason
        formState[FormField.MOTIFS] = livePickedReasons.value?.size ?: 0 > 0

        // Send the result to the view
        liveFormValidation.postValue(formState)

        // And return it
        return !formState.containsValue(false)
    }

    /**
     * List of the field in the view
     */
    enum class FormField {
        NAME,
        SURNAME,
        BIRTHDATE,
        BIRTHPLACE,
        ADDRESS,
        CITY,
        POSTAL_CODE,
        MOTIFS,
        OUT_DATE,
        OUT_TIME
    }
}