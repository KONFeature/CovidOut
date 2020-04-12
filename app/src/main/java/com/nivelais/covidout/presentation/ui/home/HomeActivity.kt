package com.nivelais.covidout.presentation.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.nivelais.covidout.R
import com.nivelais.covidout.databinding.ActivityHomeBinding
import com.nivelais.covidout.presentation.ui.create.CreateAttestationFragmentDirections
import kotlinx.android.synthetic.main.activity_home.*
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

        initView()
        initObserver()
    }

    /**
     * Init all the listener on the current view
     */
    private fun initView() {
        // Current attestations listener
        binding.btnAttestations.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(
                CreateAttestationFragmentDirections.actionCreateAttestationFragmentToAttestationsDialog()
            )
        }
    }

    /**
     * Init all the observer for the current view
     */
    private fun initObserver() {
        // Observer for the attestations count
        viewModel.liveAttestationsCount.observe(this, Observer {attestationsCount ->
            run {
                binding.textAttestationsCount.text =
                    "Vous avez actuellement ${attestationsCount.first} attestations valide sur ${attestationsCount.second} attestations"
            }
        })

        // Listen to change on the back stack, to update the text that count attestations
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.refreshAttestationsCount()
        }
    }

}