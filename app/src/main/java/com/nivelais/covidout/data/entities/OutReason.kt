package com.nivelais.covidout.data.entities

enum class OutReason(val value: String) {
    PROFESSIONEL("travail"),
    COURSES("courses"),
    SOINS("sante"),
    FAMILLE("famille"),
    SPORT("sport"),
    JUDICIAIRE("judiciaire"),
    INTERET_GENERAL("missions")
}