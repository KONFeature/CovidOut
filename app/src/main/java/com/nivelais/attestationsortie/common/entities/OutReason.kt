package com.nivelais.attestationsortie.common.entities

/**
 * Reason to go out
 */
enum class OutReason(val value: String, val code: Int) {
    PROFESSIONEL("travail", 0),
    COURSES("courses", 1),
    SOINS("sante", 2),
    FAMILLE("famille", 3),
    SPORT("sport", 4),
    JUDICIAIRE("judiciaire", 5),
    INTERET_GENERAL("missions", 6);

    companion object {
        fun fromCode(code: Int?)  = values().firstOrNull { it.code == code }?:COURSES
    }
}