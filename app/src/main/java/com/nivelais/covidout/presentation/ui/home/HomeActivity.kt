package com.nivelais.covidout.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.nivelais.covidout.common.entities.OutReason
import com.nivelais.covidout.databinding.ActivityHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

/**
 * Home activity (Create and access generated attestation)
 */
class HomeActivity : AppCompatActivity() {

    companion object {
        const val CREATE_FILE_REQUEST_CODE = 13;
    }

    /**
     * Import the view model
     */
    private val viewModel: HomeViewModel by viewModel()

    /**
     * Import the view binding
     */
    private lateinit var binding: ActivityHomeBinding

    private var fileToWrite: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initObserver()
    }

    /**
     * Init the view
     */
    private fun initView() {
        // TODO : Listener on date selectors

        // Generate button listener
        binding.btnGenerate.setOnClickListener {
            // Find the selected reason
            val outReason = when (binding.inputGroupMotif.checkedRadioButtonId) {
                binding.inputMotifCourses.id -> OutReason.COURSES
                binding.inputMotifFamille.id -> OutReason.FAMILLE
                binding.inputMotifInteretGeneral.id -> OutReason.INTERET_GENERAL
                binding.inputMotifJudiciaire.id -> OutReason.JUDICIAIRE
                binding.inputMotifPro.id -> OutReason.PROFESSIONEL
                binding.inputMotifSoins.id -> OutReason.SOINS
                binding.inputMotifSport.id -> OutReason.SPORT
                else -> OutReason.COURSES
            }

            // Launch the generation with all the field from the view
            viewModel.generateAttestation(
                binding.inputName.editText?.text.toString(),
                binding.inputSurname.editText?.text.toString(),
                binding.inputBirthDate.editText?.text.toString(),
                binding.inputBirthPlace.editText?.text.toString(),
                binding.inputAddress.editText?.text.toString(),
                binding.inputCity.editText?.text.toString(),
                binding.inputPostalCode.editText?.text.toString(),
                outReason,
                binding.inputOutDate.editText?.text.toString(),
                binding.inputOutTime.editText?.text.toString()
            )
        }
    }

    /**
     * Init live data observer
     */
    private fun initObserver() {
        // Listener for the attestions infos to load
        viewModel.liveSavedAttestation.observe(this, Observer { attestationsSaved ->
            run {
                // Set the base info
                binding.inputName.editText?.setText(attestationsSaved.name)
                binding.inputSurname.editText?.setText(attestationsSaved.surname)
                binding.inputBirthDate.editText?.setText(attestationsSaved.birthDate)
                binding.inputBirthPlace.editText?.setText(attestationsSaved.birthPlace)
                binding.inputAddress.editText?.setText(attestationsSaved.address)
                binding.inputCity.editText?.setText(attestationsSaved.city)
                binding.inputPostalCode.editText?.setText(attestationsSaved.postalCode)

                // Set the reason

                // Set outDate and outTime
                binding.inputOutDate.editText?.setText(attestationsSaved.outDate)
                binding.inputOutTime.editText?.setText(attestationsSaved.outTime)
            }
        })

        // Listener for the generate4d pdf file
        viewModel.livePdfFile.observe(this, Observer { pdfFile ->
            run {
                fileToWrite = pdfFile

                // Create an intent to create a pdf file in wich to write
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "application/pdf"

                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, "attestation derogatoire de sortie.pdf")
                }

                // Launch the intent
                startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CREATE_FILE_REQUEST_CODE && data != null) {
            val uri = data.data
            uri?.let { uri ->
                contentResolver.openFileDescriptor(uri, "w")?.let {
                    val fos = FileOutputStream(it.fileDescriptor)
                    fos.write(fileToWrite?.readBytes())
                    fos.close()
                }
            }
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}