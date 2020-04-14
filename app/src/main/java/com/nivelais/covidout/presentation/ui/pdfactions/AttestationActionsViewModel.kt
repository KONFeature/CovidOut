package com.nivelais.covidout.presentation.ui.pdfactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.common.usecases.DeleteAttestationPdfUseCase
import com.nivelais.covidout.common.usecases.GetAttestationPdfUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * View model for the actions a user can do one an attestations
 */
class AttestationActionsViewModel(
    private val getAttestationPdfUseCase: GetAttestationPdfUseCase,
    private val deleteAttestationPdfUseCase: DeleteAttestationPdfUseCase
) : ViewModel() {

    /*
    * Job and context for coroutines
    */
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    /**
     * Live data for our attestations when loaded
     */
    val attestationLive = MutableLiveData<AttestationPdfEntity>()

    /**
     * Live data to know when an attestation is deleted
     */
    val attestationDeletedLive = MutableLiveData<Void>()

    /**
     * Load an habilitations from it's id
     */
    fun loadAttestion(id: Long) {
        getAttestationPdfUseCase(scope, id) {
            if (it.isSuccess()) attestationLive.postValue(it.data)
        }
    }

    /**
     * Delete the current displayed attestation
     */
    fun deleteAttestation(id: Long) {
        deleteAttestationPdfUseCase(scope, id) {
            // Send callback to the view
            attestationDeletedLive.postValue(null)
        }
    }
}