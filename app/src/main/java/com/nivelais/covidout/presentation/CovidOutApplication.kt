package com.nivelais.covidout.presentation

import android.app.Application
import com.nivelais.covidout.presentation.di.objectboxModule
import com.nivelais.covidout.presentation.di.repositoryModule
import com.nivelais.covidout.presentation.di.useCasesModule
import com.nivelais.covidout.presentation.di.viewModelModule
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CovidOutApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Load pdf ressources
        PDFBoxResourceLoader.init(this)

        // Init koin
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@CovidOutApplication)
            modules(listOf(objectboxModule, repositoryModule, useCasesModule, viewModelModule))
        }
    }
}