package com.nivelais.attestationsortie.data.db

import com.nivelais.attestationsortie.common.entities.OutReason
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import java.util.*
import kotlin.collections.ArrayList

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
    @Convert(converter = OutReasonConverter::class, dbType = String::class)
    var outReasons: List<OutReason> = ArrayList()

)

/**
 * Converter for our outreason
 */
class OutReasonConverter : PropertyConverter<List<OutReason>, String> {

    override fun convertToDatabaseValue(entityProperty: List<OutReason>?): String {
        return entityProperty?.joinToString(";") { it.code.toString() } ?: ""
    }

    override fun convertToEntityProperty(databaseValue: String?): List<OutReason> {
        return databaseValue?.split(";")?.map { OutReason.fromCode(it.toIntOrNull()) }
            ?: ArrayList()
    }
}