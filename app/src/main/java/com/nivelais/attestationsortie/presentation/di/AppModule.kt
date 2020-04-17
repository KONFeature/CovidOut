package com.nivelais.attestationsortie.presentation.di

import android.content.Context
import com.nivelais.attestationsortie.common.repositories.PdfRepository
import com.nivelais.attestationsortie.common.repositories.PreferencesRepository
import com.nivelais.attestationsortie.common.usecases.*
import com.nivelais.attestationsortie.data.db.ObjectBox
import com.nivelais.attestationsortie.data.repositories.PdfRepositoryImpl
import com.nivelais.attestationsortie.data.repositories.PreferencesRepositoryImpl
import com.nivelais.attestationsortie.presentation.ui.create.CreateAttestationViewModel
import com.nivelais.attestationsortie.presentation.ui.home.HomeViewModel
import com.nivelais.attestationsortie.presentation.ui.list.AttestationsViewModel
import com.nivelais.attestationsortie.presentation.ui.pdfactions.AttestationActionsViewModel
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
                "attestation_prefs",
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
    single { GetAttestationsCountUseCase(get()) }
}

/**
 * Module for view models
 */
val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    single { CreateAttestationViewModel(get(), get()) }
    viewModel { AttestationActionsViewModel(get(), get()) }
    viewModel { AttestationsViewModel(get()) }
}