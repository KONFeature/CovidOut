package com.nivelais.covidout.common.entities

import java.util.*

data class AttestationPdfEntity(
    val id: Long,
    val path: String,
    val outDateTime: Date,
    val reason: OutReason
)