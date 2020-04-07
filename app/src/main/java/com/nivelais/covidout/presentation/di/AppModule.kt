package com.nivelais.covidout.presentation.di

import android.content.Context
import com.nivelais.covidout.data.repositories.PdfRepository
import com.nivelais.covidout.data.repositories.PreferencesRepository
import com.nivelais.covidout.presentation.ui.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * Module pour la base de donée
 */
val objectboxModule = module {
}

/**
 * Module for all the repository implementation
 */
val repositoryModule = module {
    single { PdfRepository(androidContext().assets, androidContext().cacheDir) }
    single {
        PreferencesRepository(
            androidContext().getSharedPreferences(
                "covidout_prefs",
                Context.MODE_PRIVATE
            )
        )
    }
}

/**
 * Module pour les view model
 */
val viewModelModule = module {
    // Home
    viewModel { HomeViewModel(get(), get()) }
}