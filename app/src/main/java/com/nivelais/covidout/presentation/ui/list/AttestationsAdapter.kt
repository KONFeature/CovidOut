package com.nivelais.covidout.presentation.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nivelais.covidout.common.entities.AttestationPdfEntity
import com.nivelais.covidout.databinding.ItemAttestationBinding
import com.nivelais.covidout.common.utils.DateUtils
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
            // TODO : Show in red text if not valid
            // Get the end of validity date and put it in the view
            Calendar.getInstance().apply {
                time = attestation.outDateTime

                // Debut de valitide
                binding.textAttestationsStartValidity.text =
                    "Debut de validite le ${DateUtils.dateTimeFormat.format(time)}"

                // Fin de validite
                add(Calendar.HOUR_OF_DAY, 1)
                binding.textAttestationsEndValidity.text =
                    "Fin de validite le ${DateUtils.dateTimeFormat.format(time)}"
            }

            // Bind the reason to the view
            val reasonsTxt = attestation.reasons.joinToString(", ") { it.value }
            binding.textAttestationsReason.text = "Raison de sortie : ${reasonsTxt}"

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