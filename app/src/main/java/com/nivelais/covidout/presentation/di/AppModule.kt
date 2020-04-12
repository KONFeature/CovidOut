package com.nivelais.covidout.presentation.di

import android.content.Context
import com.nivelais.covidout.common.repositories.PdfRepository
import com.nivelais.covidout.common.repositories.PreferencesRepository
import com.nivelais.covidout.common.usecases.*
import com.nivelais.covidout.data.db.ObjectBox
import com.nivelais.covidout.data.repositories.PdfRepositoryImpl
import com.nivelais.covidout.data.repositories.PreferencesRepositoryImpl
import com.nivelais.covidout.presentation.ui.create.CreateAttestationViewModel
import com.nivelais.covidout.presentation.ui.home.HomeViewModel
import com.nivelais.covidout.presentation.ui.list.AttestationsViewModel
import com.nivelais.covidout.presentation.ui.pdfactions.AttestationActionsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/**
 * Module for the database
 */
val objectboxModule = module {
    single { ObjectBox.init(androidContext()) }
}

/**
 * Module for all the repository implementation
 */
val repositoryModule = module {
    single {
        PdfRepositoryImpl(
            androidContext().assets,
            androidContext().filesDir,
            get()
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
    single { GeneratePdfUseCase(get(), get()) }
    single { GetAttestationPdfUseCase(get()) }
    single { FetchAttestationsUseCase(get()) }
    single { DeleteAttestationPdfUseCase(get()) }
    single { GetDefaultAttestationUseCase(get()) }
}

/**
 * Module for view models
 */
val viewModelModule = module {
    viewModel { HomeViewModel() }
    single { CreateAttestationViewModel(get(), get()) }
    viewModel { AttestationActionsViewModel(get(), get()) }
    viewModel { AttestationsViewModel(get()) }
}