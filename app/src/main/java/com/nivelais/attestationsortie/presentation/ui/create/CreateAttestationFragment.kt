package com.nivelais.attestationsortie.presentation.ui.create

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.nivelais.attestationsortie.R
import com.nivelais.attestationsortie.common.utils.DateUtils
import com.nivelais.attestationsortie.databinding.FragmentCreateAttestationBinding
import com.nivelais.attestationsortie.presentation.ui.home.HomeViewModel
import com.nivelais.attestationsortie.presentation.ui.pdfactions.AttestationActionsDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * Fragment used to create new attestations
 */
class CreateAttestationFragment : Fragment() {

    /**
     * Import the view model
     */
    private val viewModel: CreateAttestationViewModel by inject()

    /**
     * Import the global view model
     */
    private val sharedViewModel: HomeViewModel by sharedViewModel()

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
        // Listener on the pick reasons click
        binding.btnPickReasons.setOnClickListener {
            // Launch the reason picker dialog
            PickReasonsDialog().show(parentFragmentManager, null)
        }

        // Listener on Birth date calender button
        binding.btnBirthdatePicker.setOnClickListener {
            // Try to parse the date, or init it at 01/01/1990
            val currentInput = binding.inputBirthDate.editText?.text?.toString()
            val inputToParse =
                if (DateUtils.isValidDate(currentInput)) currentInput!! else "01/01/1990"
            val currentDate =
                DateUtils.dateFormat.parse(inputToParse)!! // Not null, because we have an exception if that's null

            // Max moth = current month & open at startTime
            val pickerConstraints = CalendarConstraints
                .Builder()
                .setOpenAt(currentDate.time)
                .setEnd(System.currentTimeMillis())
                .build()
            // Create the picker with all the constraints
            val picker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(pickerConstraints)
                .setTitleText("Date de naissance")
                .setSelection(currentDate.time)
                .build()
            // Listen to picker validation
            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val selectedDate = Date(selectedDateInMillis)
                binding.inputBirthDate.editText?.setText(DateUtils.dateFormat.format(selectedDate))
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
                val selectedDate = Date(selectedDateInMillis)
                binding.inputOutDate.editText?.setText(DateUtils.dateFormat.format(selectedDate))
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

        // Generate button listener
        binding.btnGenerate.setOnClickListener {
            // Launch the generation with all the field from the view
            viewModel.generateAttestation(
                binding.inputName.editText?.text.toString(),
                binding.inputSurname.editText?.text.toString(),
                binding.inputBirthDate.editText?.text.toString(),
                binding.inputBirthPlace.editText?.text.toString(),
                binding.inputAddress.editText?.text.toString(),
                binding.inputCity.editText?.text.toString(),
                binding.inputPostalCode.editText?.text.toString(),
                binding.inputOutDate.editText?.text.toString(),
                binding.inputOutTime.editText?.text.toString()
            )
        }
    }

    /**
     * Init live data observer
     */
    private fun initObserver() {
        // Observer for the picked reasons
        viewModel.livePickedReasons.observe(viewLifecycleOwner, Observer { reasons ->
            run {
                if (reasons.size > 0) {
                    val pickedReasonsText = reasons.joinToString(", ") { it.value }
                    binding.textPickedMotif.text =
                        getString(R.string.lbl_actual_out_reason, pickedReasonsText)
                } else {
                    binding.textPickedMotif.text = getString(R.string.lbl_no_out_reason)
                }
            }
        })

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

                // Set outDate and outTime
                binding.inputOutDate.editText?.setText(attestationsSaved.outDate)
                binding.inputOutTime.editText?.setText(attestationsSaved.outTime)
            }

        })

        // Listener for the generated pdf file
        viewModel.liveGeneratedPdfId.observe(viewLifecycleOwner, Observer { pdfId ->
            run {
                // Tell the global view model that the list of files as changed
                sharedViewModel.refreshAttestationsCount()
                // Open the dialog to let the user choose what he want to do with the generated file
                AttestationActionsDialog(pdfId).show(parentFragmentManager, null)
            }
        })

        // Listener on the form validation
        viewModel.liveFormValidation.observe(viewLifecycleOwner, Observer { formFieldValidMap ->
            run {
                formFieldValidMap.forEach { fieldValid ->
                    // Display or hide the error on the field
                    if (fieldValid.value)
                        clearFieldInError(fieldValid.key)
                    else
                        setFieldInError(fieldValid.key)
                }
            }
        })
    }

    /**
     * Set a field in error
     */
    private fun setFieldInError(field: CreateAttestationViewModel.FormField) {
        when (field) {
            CreateAttestationViewModel.FormField.NAME -> {
                binding.inputName.error = "Merci de remplir de champ"
                binding.inputName.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.SURNAME -> {
                binding.inputSurname.error = "Merci de remplir de champ"
                binding.inputSurname.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.BIRTHDATE -> {
                binding.inputBirthDate.error =
                    "Merci de remplir de champ au format JJ/MM/AAA (17/06/1998 par exemple)"
                binding.inputBirthDate.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.BIRTHPLACE -> {
                binding.inputBirthPlace.error = "Merci de remplir de champ"
                binding.inputBirthPlace.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.ADDRESS -> {
                binding.inputAddress.error = "Merci de remplir de champ"
                binding.inputAddress.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.CITY -> {
                binding.inputCity.error = "Merci de remplir de champ"
                binding.inputCity.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.POSTAL_CODE -> {
                binding.inputPostalCode.error = "Merci de remplir de champ"
                binding.inputPostalCode.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.OUT_DATE -> {
                binding.inputOutDate.error =
                    "Merci de remplir de champ au format JJ/MM/AAA (17/06/1998 par exemple)"
                binding.inputOutDate.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.OUT_TIME -> {
                binding.inputOutTime.error =
                    "Merci de remplir de champ au format HH:MM (16:20 par exemple)"
                binding.inputOutTime.isErrorEnabled = true
            }
            CreateAttestationViewModel.FormField.MOTIFS -> {
                binding.textPickedMotifErr.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Clear field error
     */
    private fun clearFieldInError(field: CreateAttestationViewModel.FormField) {
        when (field) {
            CreateAttestationViewModel.FormField.NAME -> {
                binding.inputName.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.SURNAME -> {
                binding.inputSurname.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.BIRTHDATE -> {
                binding.inputBirthDate.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.BIRTHPLACE -> {
                binding.inputBirthPlace.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.ADDRESS -> {
                binding.inputAddress.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.CITY -> {
                binding.inputCity.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.POSTAL_CODE -> {
                binding.inputPostalCode.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.OUT_DATE -> {
                binding.inputOutDate.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.OUT_TIME -> {
                binding.inputOutTime.isErrorEnabled = false
            }
            CreateAttestationViewModel.FormField.MOTIFS -> {
                binding.textPickedMotifErr.visibility = View.GONE
            }
        }
    }
}
