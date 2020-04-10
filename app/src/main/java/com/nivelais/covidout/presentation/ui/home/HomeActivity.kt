package com.nivelais.covidout.presentation.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nivelais.covidout.databinding.ActivityHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Home activity (Create and access generated attestation)
 */
class HomeActivity : AppCompatActivity() {

    /**
     * Import the view model
     */
    private val viewModel: HomeViewModel by viewModel()

    /**
     * Import the view binding
     */
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}