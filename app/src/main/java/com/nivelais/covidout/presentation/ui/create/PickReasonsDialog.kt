package com.nivelais.covidout.presentation.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nivelais.covidout.common.entities.OutReason
import com.nivelais.covidout.databinding.DialogPickReasonsBinding
import org.koin.android.ext.android.inject

class PickReasonsDialog() : BottomSheetDialogFragment() {

    /**
     * Fetch the create attestions view model,
     */
    private val viewModel: CreateAttestationViewModel by inject()


    /**
     * Import the view binding
     */
    private lateinit var binding: DialogPickReasonsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogPickReasonsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init checked checkbox
        viewModel.livePickedReasons.value?.forEach {
            when (it) {
                OutReason.PROFESSIONEL -> binding.inputMotifPro.isChecked = true
                OutReason.COURSES -> binding.inputMotifCourses.isChecked = true
                OutReason.SOINS -> binding.inputMotifSoins.isChecked = true
                OutReason.FAMILLE -> binding.inputMotifFamille.isChecked = true
                OutReason.SPORT -> binding.inputMotifSport.isChecked = true
                OutReason.JUDICIAIRE -> binding.inputMotifJudiciaire.isChecked = true
                OutReason.INTERET_GENERAL -> binding.inputMotifInteretGeneral.isChecked = true
            }
        }

        // Listen on the validate button
        binding.btnValidate.setOnClickListener { dismiss() }

        // Listen on the different checkbox
        binding.inputMotifCourses.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.COURSES, isChecked)
        }
        binding.inputMotifFamille.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.FAMILLE, isChecked)
        }
        binding.inputMotifInteretGeneral.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.INTERET_GENERAL, isChecked)
        }
        binding.inputMotifSport.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.SPORT, isChecked)
        }
        binding.inputMotifPro.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.PROFESSIONEL, isChecked)
        }
        binding.inputMotifJudiciaire.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.JUDICIAIRE, isChecked)
        }
        binding.inputMotifSoins.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.savePickedReason(OutReason.SOINS, isChecked)
        }
    }
}