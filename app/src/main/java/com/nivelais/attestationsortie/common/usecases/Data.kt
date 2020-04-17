package com.nivelais.attestationsortie.common.usecases

/**
 * A generic wrapper class around data request
 */
data class Data<RequestData>(
    var responseType: Status,
    var data: RequestData? = null,
    var error: Error? = null
) {
    companion object {
        // Simple function to post a success request
        fun <RequestData> succes(data: RequestData) =
            Data(
                Status.SUCCESSFUL,
                data,
                null
            )
    }

    fun isLoading() = responseType == Status.LOADING
    fun isSuccess() = responseType == Status.SUCCESSFUL
    fun isError() = responseType == Status.ERROR

}

enum class Status { SUCCESSFUL, ERROR, LOADING }

data class Error(var message: String? = null, var exception: Exception? = null)