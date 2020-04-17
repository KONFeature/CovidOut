package com.nivelais.attestationsortie.common.entities

data class AttestationEntity(
    val name: String,
    val surname: String,
    val birthDate: String,
    val birthPlace: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val outReasons: List<OutReason>,
    var outDate: String,
    var outTime: String
)