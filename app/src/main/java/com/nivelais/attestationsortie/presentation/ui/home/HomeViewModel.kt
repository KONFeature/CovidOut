package com.nivelais.attestationsortie.presentation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nivelais.attestationsortie.common.usecases.GetAttestationsCountUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    private val getAttestationsCountUseCase: GetAttestationsCountUseCase
) : ViewModel() {

    /*
    * Job and context for coroutines
    */
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    /**
     * Live data for the attestations counts
     */
    val liveAttestationsCount = MutableLiveData(Pair(0, 0))

    init {
        refreshAttestationsCount()
    }

    /**
     * Refresh the attestations counts
     */
    fun refreshAttestationsCount() {
        // Count the valid attestations
        getAttestationsCountUseCase(scope, Unit) { result ->
            if (result.isSuccess())
                liveAttestationsCount.postValue(result.data)
        }
    }

}