package com.nivelais.covidout.common.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    final val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    final val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
    final val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy 'a' HH:mm", Locale.FRANCE)


}