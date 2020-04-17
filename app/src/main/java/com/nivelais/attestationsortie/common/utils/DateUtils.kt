package com.nivelais.attestationsortie.common.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy 'a' HH:mm", Locale.FRANCE)


    /**
     * Check if a string is in a valid date pattern
     */
    fun isValidDate(toTest: String?): Boolean {
        if (toTest == null || toTest.isEmpty()) return false
        return try {
            dateFormat.parse(toTest) != null
        } catch (e: ParseException) {
            false
        }
    }

    /**
     * Check if a string is in a valid time pattern
     */
    fun isValidTime(toTest: String?): Boolean {
        if (toTest == null || toTest.isEmpty()) return false
        return try {
            timeFormat.parse(toTest) != null
        } catch (e: ParseException) {
            false
        }
    }
}