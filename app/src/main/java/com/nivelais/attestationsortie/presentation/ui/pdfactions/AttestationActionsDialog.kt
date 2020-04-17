package com.nivelais.attestationsortie.presentation.ui.pdfactions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nivelais.attestationsortie.R
import com.nivelais.attestationsortie.common.entities.AttestationPdfEntity
import com.nivelais.attestationsortie.common.utils.DateUtils
import com.nivelais.attestationsortie.databinding.DialogAttestaionActionsBinding
import com.nivelais.attestationsortie.presentation.openViaIntent
import com.nivelais.attestationsortie.presentation.shareViaIntent
import com.nivelais.attestationsortie.presentation.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AttestationActionsDialog(private val attestationId: Long) : BottomSheetDialogFragment() {

    companion object {
        const val CREATE_FILE_REQUEST_CODE = 13
    }

    /**
     * Import the view model
     */
    private val viewModel: AttestationActionsViewModel by viewModel()

    /**
     * Import the global view model
     */
    private val sharedViewModel: HomeViewModel by sharedViewModel()


    /**
     * Import the view binding
     */
    private lateinit var binding: DialogAttestaionActionsBinding

    /**
     * The file concerned by this attestations
     */
    private lateinit var attestationFile: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAttestaionActionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ask the view model to load the attestations
        viewModel.loadAttestion(attestationId)

        // Listen to the attestations live
        viewModel.attestationLive.observe(viewLifecycleOwner, Observer {
            loadAttestationToView(it)
        })

        // Listener on delete button
        binding.btnDelete.setOnClickListener {
            // Launch the deletion
            viewModel.deleteAttestation(attestationId)
        }

        // Listen to know when an attestatioon deleted
        viewModel.attestationDeletedLive.observe(viewLifecycleOwner, Observer {
            // Prevent the global view for this update
            sharedViewModel.refreshAttestationsCount()
            // dismiss this dialog
            dismiss()
        })
    }

    /**
     * Load the attestations file into the view
     */
    private fun loadAttestationToView(attestation: AttestationPdfEntity) {
        // Set the generation date
        val reasonsTxt = attestation.reasons.joinToString(", ") { it.value }
        binding.textAttestationReason.text = getString(R.string.lbl_out_reason, reasonsTxt)

        // Set the start and end of validity
        Calendar.getInstance().apply {
            time = attestation.outDateTime

            // Debut de valitide
            binding.textAttestationStartValidity.text =
                getString(R.string.lbl_start_validity, DateUtils.dateTimeFormat.format(time))

            // Fin de validite
            add(Calendar.HOUR_OF_DAY, 1)
            binding.textAttestationEndValidity.text =
                getString(R.string.lbl_end_validity, DateUtils.dateTimeFormat.format(time))
        }

        // Get the file matching the attestations
        attestationFile = File(attestation.path)

        // listener on open button
        binding.btnOpen.setOnClickListener {
            attestationFile.openViaIntent(context!!)
        }

        // listener on share button
        binding.btnShare.setOnClickListener {
            attestationFile.shareViaIntent(context!!)
        }

        // listener on save button
        binding.btnSave.setOnClickListener {
            // Create an intent to create a pdf file in wich we will write the attestation
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                type = "application/pdf"

                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_TITLE, attestationFile.name)
            }

            // Launch the intent
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // If that's a result of the pick file inent
        if (requestCode == CREATE_FILE_REQUEST_CODE && data != null) {
            data.data?.let { uri ->
                // Write to the file picked by the user
                context?.contentResolver?.openFileDescriptor(uri, "w")?.let {
                    val fos = FileOutputStream(it.fileDescriptor)
                    fos.write(attestationFile.readBytes())
                    fos.close()
                }
            }
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}