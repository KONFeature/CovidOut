package com.nivelais.attestationsortie.common.entities

data class PersonnalInformationEntity(
    val name: String,
    val surname: String,
    val birthDate: String,
    val birthPlace: String,
    val address: String,
    val city: String,
    val postalCode: String
)