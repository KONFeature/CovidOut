package com.nivelais.covidout.data.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class AttestationPdfDbEntity(
    @Id
    var id: Long = 0,

    /**
     * Path to access the attestations in the internal storage
     */
    var path: String? = null,

    /**
     * The time in wich the user will be out for this attestations
     */
    var outDateTime: Date? = null,

    /**
     * The code of the out reason of this attestation
     */
    var reasonCode: Int? = null

)