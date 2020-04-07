package com.nivelais.covidout.presentation.ui.home

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.covidout.data.entities.Attestation
import com.nivelais.covidout.data.entities.OutReason
import com.nivelais.covidout.data.repositories.PdfRepository
import com.nivelais.covidout.data.repositories.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hamcrest.core.SubstringMatcher
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    private val pdfRepository: PdfRepository,
    private val preferencesRepository: PreferencesRepository
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
    val livePdfFile = MutableLiveData<File>()

    /**
     * Live data that return the saved attestions infos
     */
    val liveSavedAttestation = MutableLiveData<Attestation>()

    init {
        // Find and push the saved attestations
        scope.launch {
            // Date format for the outDate and outTime
            val outDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val outTimeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

            // Post the value
            liveSavedAttestation.postValue(
                Attestation(
                    preferencesRepository.name,
                    preferencesRepository.surname,
                    preferencesRepository.birthdate,
                    preferencesRepository.birthplace,
                    preferencesRepository.address,
                    preferencesRepository.city,
                    preferencesRepository.postalCode,
                    OutReason.COURSES,
                    outDateFormat.format(Date()),
                    outTimeFormat.format(Date())
                )
            )
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
        scope.launch {
            // Bqckup this infos in preferences
            preferencesRepository.apply {
                this.name = name
                this.surname = surname
                this.birthdate = birthdate
                this.birthplace = birthplace
                this.address = address
                this.city = city
                this.postalCode = postalCode
            }

            // Generate the pdf file
            val generatedPdf = pdfRepository.generate(
                Attestation(
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
            )

            // Send it to the presentation view
            livePdfFile.postValue(generatedPdf)
        }
    }

    /**
     * Retreive the out date for now
     */
    fun getBaseOutDate() {

    }

    /**
     * Retreive the out time for now
     */
    fun getBaseOutTime() {

    }
}