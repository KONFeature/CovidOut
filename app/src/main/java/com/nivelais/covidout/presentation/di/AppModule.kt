package com.nivelais.covidout.presentation.di

import android.content.Context
import com.nivelais.covidout.common.repositories.PdfRepository
import com.nivelais.covidout.common.repositories.PreferencesRepository
import com.nivelais.covidout.common.usecases.GeneratePdfUseCase
import com.nivelais.covidout.common.usecases.LoadAttestationDatasUseCase
import com.nivelais.covidout.common.usecases.SaveAttestationDatasUseCase
import com.nivelais.covidout.data.repositories.PdfRepositoryImpl
import com.nivelais.covidout.data.repositories.PreferencesRepositoryImpl
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
    single {
        PdfRepositoryImpl(
            androidContext().assets,
            androidContext().cacheDir
        ) as PdfRepository
    }
    single {
        PreferencesRepositoryImpl(
            androidContext().getSharedPreferences(
                "covidout_prefs",
                Context.MODE_PRIVATE
            )
        ) as PreferencesRepository
    }
}

/**
 * Module for all the repository implementation
 */
val useCasesModule = module {
    single { GeneratePdfUseCase(get()) }
    single { SaveAttestationDatasUseCase(get()) }
    single { LoadAttestationDatasUseCase(get()) }
}

/**
 * Module pour les view model
 */
val viewModelModule = module {
    // Home
    viewModel { HomeViewModel(get(), get(), get()) }
}