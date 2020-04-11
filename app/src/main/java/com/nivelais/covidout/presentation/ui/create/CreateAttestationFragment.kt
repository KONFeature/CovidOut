package com.nivelais.covidout.presentation.ui.create

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.nivelais.covidout.common.entities.OutReason
import com.nivelais.covidout.databinding.FragmentCreateAttestationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 * Fragment used to create new attestations
 */
class CreateAttestationFragment() : Fragment() {

    /**
     * Import the view model
     */
    private val viewModel: CreateAttestationViewModel by viewModel()

    /**
     * Import the view binding
     */
    private lateinit var binding: FragmentCreateAttestationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAttestationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
    }

    /**
     * Init the view
     */
    private fun initView() {
        // Listener on Birth date calender button
        binding.btnBirthdatePicker.setOnClickListener {
            // Start the picker on the current value, or to 01/01/1990 iw we don't find a current value
            val startDate = Calendar.getInstance().apply {
                val currentInputSplitted =
                    binding.inputBirthDate.editText?.text?.toString()?.split("/")
                set(Calendar.DAY_OF_MONTH, currentInputSplitted?.get(0)?.toIntOrNull() ?: 1990)
                set(Calendar.MONTH, currentInputSplitted?.get(1)?.toIntOrNull()?.minus(1) ?: 1)
                set(Calendar.YEAR, currentInputSplitted?.get(2)?.toIntOrNull() ?: 1)
            }.timeInMillis
            // Max moth = current month & open at startTime
            val pickerConstraints = CalendarConstraints
                .Builder()
                .setOpenAt(startDate)
                .setEnd(System.currentTimeMillis())
                .build()
            // Create the picker with all the constraints
            val picker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(pickerConstraints)
                .setTitleText("Date de naissance")
                .setSelection(startDate)
                .build()
            // Listen to picker validation
            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                Calendar.getInstance().apply {
                    timeInMillis = selectedDateInMillis

                    // Fetch day / moth / year
                    val day = get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                    val month = get(Calendar.MONTH).toString().padStart(2, '0')
                    val year = get(Calendar.YEAR).toString()

                    // Add them is the edittext
                    binding.inputBirthDate.editText?.setText("$day/$month/$year")
                }
            }
            // Display it
            picker.show(parentFragmentManager, picker.toString())
        }

        // Listener on out Date calender button
        binding.btnOutDatePicker.setOnClickListener {
            // Start month = current month
            val pickerConstraints = CalendarConstraints
                .Builder()
                .setStart(System.currentTimeMillis())
                .build()
            // Create the picker with all the constraint
            val picker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(pickerConstraints)
                .setTitleText("Date de sortie")
                .setSelection(System.currentTimeMillis())
                .build()
            // Listen to the picker validation
            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                Calendar.getInstance().apply {
                    timeInMillis = selectedDateInMillis

                    // Fetch day / moth / year
                    val day = get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                    val month = get(Calendar.MONTH).toString().padStart(2, '0')
                    val year = get(Calendar.YEAR).toString()

                    // Add them is the edittext
                    binding.inputOutDate.editText?.setText("$day/$month/$year")
                }
            }
            // Show it
            picker.show(parentFragmentManager, picker.toString())
        }

        // Listener on the out time button
        binding.btnOutTimePicker.setOnClickListener {
            // Get the start minute and hour of the time pciker
            val startMinute = Calendar.getInstance().get(Calendar.MINUTE)
            val startHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            // Create the popup and show it
            TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    run {
                        // Put the result in the edittext
                        val outTime =
                            hourOfDay.toString().padStart(2, '0') + ":" + minute.toString()
                                .padStart(2, '0')
                        binding.inputOutTime.editText?.setText(outTime)
                    }
                },
                startHour,
                startMinute,
                true
            ).show()
        }

        // Old attestations listener
        binding.btnAttestations.setOnClickListener {
            findNavController().navigate(
                CreateAttestationFragmentDirections.actionCreateAttestationFragmentToAttestationsDialog()
            )
        }

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
        viewModel.liveSavedAttestation.observe(viewLifecycleOwner, Observer { attestationsSaved ->
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
                val test: Long

                // Set outDate and outTime
                binding.inputOutDate.editText?.setText(attestationsSaved.outDate)
                binding.inputOutTime.editText?.setText(attestationsSaved.outTime)
            }

        })

        // Listener for the generate4d pdf file
        viewModel.livePdfIdFile.observe(viewLifecycleOwner, Observer { pdfId ->
            run {
                // Open the dialog to let the user choose what he want to do with the generated file
                findNavController().navigate(
                    CreateAttestationFragmentDirections.actionCreateAttestationFragmentToPdfActionsDialog(
                        pdfId
                    )
                )
            }
        })
    }
}