package com.nivelais.covidout.presentation.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nivelais.covidout.databinding.DialogAttestationsBinding
import com.nivelais.covidout.presentation.openViaIntent
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

/**
 * Fragment that list the current and past attestations
 */
class AttestationsDialog() : BottomSheetDialogFragment() {

    /**
     * Import the view model
     */
    private val viewModel: AttestationsViewModel by viewModel()

    /**
     * Import the view binding
     */
    private lateinit var binding: DialogAttestationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DialogAttestationsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen to the view model observer
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
                        findNavController().navigate(
                            AttestationsDialogDirections.actionAttestationsDialogToAttestationActionsDialog(
                                attestationId = attestationClicked.id
                            )
                        )
                    }
                )
            }
        })

        // Listen to the backstack update to update the list
        findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.updateAttestations()
        }
    }
}