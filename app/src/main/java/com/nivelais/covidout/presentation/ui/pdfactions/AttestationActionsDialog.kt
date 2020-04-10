package com.nivelais.covidout.presentation.ui.pdfactions

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.databinding.DialogAttestaionActionsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AttestationActionsDialog() : BottomSheetDialogFragment() {

    companion object {
        const val CREATE_FILE_REQUEST_CODE = 13;
    }

    /**
     * Import the view model
     */
    private val viewModel: AttestationActionsViewModel by viewModel()

    /**
     * Import the view binding
     */
    private lateinit var binding: DialogAttestaionActionsBinding

    /**
     * The file concerned by this attestations
     */
    private lateinit var attestationFile: File

    /**
     * The arguments for this dialog
     */
    val args: AttestationActionsDialogArgs by navArgs()

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
        viewModel.loadAttestion(args.attestationId)

        // Listen to the attestations live
        viewModel.attestationLive.observe(viewLifecycleOwner, Observer {
            loadAttestationToView(it)
        })

        // Load the pdf file


        // Listener on button

        // Maybe view model ?
    }

    /**
     * Load the attestations file into the view
     */
    private fun loadAttestationToView(attestation: AttestationPdfEntity) {
        // Set the generation date
        val generatedFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        binding.textAttestationGenerationDate.text =
            "Generer le : ${generatedFormat.format(attestation.outDateTime)}"

        // Set the end of validity
        Calendar.getInstance().apply {
            time = attestation.outDateTime
            add(Calendar.HOUR_OF_DAY, 1)

            val validityFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
            binding.textAttestationEndValidity.text =
                "Valide jusqu'a ${validityFormat.format(time)}"
        }

        // Get the file matching the attestations
        attestationFile = File(attestation.path)

        // listener on open button
        binding.btnOpen.setOnClickListener {
            launchOpenAttestationIntent()
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
            val uri = data.data
            uri?.let { uri ->
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

    /**
     * Create and launch an intent that will try to open the attestations
     */
    private fun launchOpenAttestationIntent() {
        // Create the uri and the intent to open the attestations
        val uri =
            FileProvider.getUriForFile(
                context!!,
                "com.nivelais.covidout.provider",
                attestationFile
            )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        // Approve all the potential openner of the file
        val resInfoList = activity?.packageManager?.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (resolveInfo in resInfoList ?: ArrayList()) {
            val packageName = resolveInfo.activityInfo.packageName
            activity?.grantUriPermission(
                packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        // Launch the openning of the attestations
        startActivity(intent)
    }
}