package com.nivelais.covidout.common.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    final val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    final val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
    final val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy 'a' HH:mm", Locale.FRANCE)

    const val datePattern = "([0-9]{2})/([0-9]{2})/([0-9]{4})"
    const val timePattern = "([0-9]{2}):([0-9]{2})"


    /**
     * Check if a string is in a valid date pattern
     */
    public fun isValidDate(toTest: String?): Boolean {
        if (toTest == null || toTest.isEmpty()) return false
        return try {
            dateFormat.parse(toTest) != null
        } catch (e: ParseException) {
            false;
        }
    }

    /**
     * Check if a string is in a valid time pattern
     */
    public fun isValidTime(toTest: String?): Boolean {
        if (toTest == null || toTest.isEmpty()) return false
        return try {
            timeFormat.parse(toTest) != null
        } catch (e: ParseException) {
            false;
        }
    }
}