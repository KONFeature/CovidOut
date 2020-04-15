package com.nivelais.covidout.presentation.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nivelais.covidout.R
import com.nivelais.covidout.databinding.FragmentAttestationsBinding
import com.nivelais.covidout.presentation.openViaIntent
import com.nivelais.covidout.presentation.ui.home.HomeViewModel
import com.nivelais.covidout.presentation.ui.pdfactions.AttestationActionsDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

/**
 * Fragment that list the current and past attestations
 */
class AttestationsFragment : Fragment() {

    /**
     * Import the view model
     */
    private val viewModel: AttestationsViewModel by viewModel()

    /**
     * Import the global view model
     */
    private val sharedViewModel: HomeViewModel by sharedViewModel()

    /**
     * Import the view binding
     */
    private lateinit var binding: FragmentAttestationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttestationsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen to the attestations live
        viewModel.liveAttestations.observe(viewLifecycleOwner, Observer { attestations ->
            run {
                // Init the recycler view
                binding.listAttestations.layoutManager = LinearLayoutManager(context)
                binding.listAttestations.adapter = AttestationsAdapter(
                    attestations,
                    { attestationClicked ->
                        // Open the file via an intent
                        val attestationFile = File(attestationClicked.path)
                        attestationFile.openViaIntent(context!!)
                    },
                    { attestationClicked ->
                        // Open the menu for the attestation
                        AttestationActionsDialog(attestationClicked.id).show(
                            parentFragmentManager,
                            null
                        )
                    }
                )
            }
        })

        // Observer for the attestations count
        sharedViewModel.liveAttestationsCount.observe(viewLifecycleOwner, Observer { attestationsCount ->
            run {
                // Update text
                val textToUse = if(attestationsCount.first > 1) R.string.lbl_attestations_count else R.string.lbl_attestation_count
                binding.textAttestationsCount.text =
                    getString(
                        textToUse,
                        attestationsCount.first,
                        attestationsCount.second
                    )
                // Update the list
                viewModel.updateAttestations()
            }
        })
    }
}