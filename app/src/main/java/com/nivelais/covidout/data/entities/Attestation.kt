package com.nivelais.covidout.data.entities

data class Attestation(
    val name: String,
    val surname: String,
    val birthDate: String,
    val birthPlace: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val outReason: OutReason,
    val outDate: String,
    val outTime: String
)