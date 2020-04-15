package com.nivelais.covidout.data.repositories

import android.content.SharedPreferences
import com.nivelais.covidout.common.entities.PersonnalInformationEntity
import com.nivelais.covidout.common.repositories.PreferencesRepository

/**
 * Repository to interact with all of our shared preferences
 */
class PreferencesRepositoryImpl(private val prefs: SharedPreferences) : PreferencesRepository {

    companion object {
        const val KEY_NAME = "name"
        const val KEY_SURNAME = "surname"
        const val KEY_BIRTHDAY = "birthday"
        const val KEY_BIRTHPLACE = "birthplace"
        const val KEY_ADDRESS = "ADDRESS"
        const val KEY_POSTAL_CODE = "postal_code"
        const val KEY_CITY = "city"
    }

    private var name: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    private var surname: String
        get() = prefs.getString(KEY_SURNAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SURNAME, value).apply()

    private var birthdate: String
        get() = prefs.getString(KEY_BIRTHDAY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_BIRTHDAY, value).apply()

    private var birthplace: String
        get() = prefs.getString(KEY_BIRTHPLACE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_BIRTHPLACE, value).apply()

    private var address: String
        get() = prefs.getString(KEY_ADDRESS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ADDRESS, value).apply()

    private var postalCode: String
        get() = prefs.getString(KEY_POSTAL_CODE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_POSTAL_CODE, value).apply()

    private var city: String
        get() = prefs.getString(KEY_CITY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CITY, value).apply()


    override suspend fun savePersonalInformation(information: PersonnalInformationEntity) {
        name = information.name
        surname = information.surname
        birthdate = information.birthDate
        birthplace = information.birthPlace
        address = information.address
        city = information.city
        postalCode = information.postalCode
    }


    override suspend fun getPersonalInformation() =
        PersonnalInformationEntity(
            name,
            surname,
            birthdate,
            birthplace,
            address,
            city,
            postalCode
        )

}