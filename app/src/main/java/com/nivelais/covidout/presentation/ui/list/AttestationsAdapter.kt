package com.nivelais.covidout.presentation.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.databinding.ItemAttestationBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for attestations items in the launch screen
 */
class AttestationsAdapter(
    private var attestations: List<AttestationPdfEntity>,
    private val onOpenClick: ((AttestationPdfEntity) -> Unit),
    private val omMoreClick: ((AttestationPdfEntity) -> Unit)
) : RecyclerView.Adapter<AttestationsAdapter.AttestationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttestationsViewHolder =
        AttestationsViewHolder(
            ItemAttestationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = attestations.size

    override fun onBindViewHolder(holder: AttestationsViewHolder, position: Int) =
        holder.bind(attestations[position], onOpenClick, omMoreClick)


    /**
     * Update the attestations list
     */
    fun updateAttestations(attestations: List<AttestationPdfEntity>) {
        this.attestations = attestations
        notifyDataSetChanged()
    }

    /**
     * View Holder for a kfile item
     */
    class AttestationsViewHolder(private val binding: ItemAttestationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind a KFile object to the view
         */
        fun bind(
            attestation: AttestationPdfEntity,
            onOpenClick: ((AttestationPdfEntity) -> Unit),
            omMoreClick: ((AttestationPdfEntity) -> Unit)
        ) {
            // TODO : Start time too
            // TODO : Show in red text if not valid
            // Get the end of validity date and put it in the view
            Calendar.getInstance().apply {
                time = attestation.outDateTime
                add(Calendar.HOUR_OF_DAY, 1)

                val validityFormat = SimpleDateFormat("dd/MM/yyyy 'a' HH:mm", Locale.FRANCE)
                binding.textAttestationsEndValidity.text =
                    "Fin de validite le ${validityFormat.format(time)}"
            }

            // Bind the reason to the view
            binding.textAttestationsReason.text = "Raison de sortie : ${attestation.reason.value}"

            // Bind the action to the listener
            binding.btnOpen.setOnClickListener {
                onOpenClick.invoke(attestation)
            }
            binding.btnMore.setOnClickListener {
                omMoreClick.invoke(attestation)
            }
        }
    }
}