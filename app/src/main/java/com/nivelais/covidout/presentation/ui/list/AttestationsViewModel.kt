package com.nivelais.covidout.presentation.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.common.usecases.FetchAttestationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * View model for the fragment
 */
class AttestationsViewModel(
    private val fetchAttestationsUseCase: FetchAttestationsUseCase
) : ViewModel() {
    /*
    * Job and context for coroutines
    */
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    /**
     * Live data with all the recent attestations
     */
    val liveAttestations = MutableLiveData<List<AttestationPdfEntity>>()

    init {
        // Search for attestations generated
        fetchAttestationsUseCase(scope, Unit) {
            if (it.isSuccess()) liveAttestations.postValue(it.data)
        }
    }
}