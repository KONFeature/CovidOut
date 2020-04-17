package com.nivelais.attestationsortie.data.repositories

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.nivelais.attestationsortie.common.entities.AttestationEntity
import com.nivelais.attestationsortie.common.entities.AttestationPdfEntity
import com.nivelais.attestationsortie.common.entities.OutReason
import com.nivelais.attestationsortie.common.repositories.PdfRepository
import com.nivelais.attestationsortie.data.addBitmap
import com.nivelais.attestationsortie.data.db.AttestationPdfDbEntity
import com.nivelais.attestationsortie.data.generateBitmap
import com.nivelais.attestationsortie.data.writeText
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Repository class used to generate PDF
 */
class PdfRepositoryImpl(
    private val assetManager: AssetManager,
    pdfInternalDir: File,
    boxStore: BoxStore
) : PdfRepository {

    companion object {
        // Template file to load from the assets
        private const val TEMPLATE_NAME = "attestation-deplacement.pdf"

        // Folder to store PDF file
        private const val INTENAL_FOLDER_NAME = "generated-pdf"
    }

    /**
     * Folder in wich we will store generated PDF File
     */
    private val pdfFolder = File(pdfInternalDir, INTENAL_FOLDER_NAME)

    /**
     * Access to our database
     */
    private val dao: Box<AttestationPdfDbEntity> = boxStore.boxFor()

    override suspend fun generate(attestation: AttestationEntity): Long {
        // Get the template document and first page
        val document = PDDocument.load(assetManager.open(TEMPLATE_NAME))
        val page = document.getPage(0)


        // Add some text
        val generatedDate = Date()
        writeAttestationInfos(attestation, generatedDate, document, page)

        // Generate and add the QrCode
        val qrCodeData = generateQrCodeData(attestation, generatedDate)
        writeQrCode(qrCodeData, document, page)

        // Generate an output file
        val outFile = File(pdfFolder, "attestations-deplacement-${System.currentTimeMillis()}.pdf")

        // Check if the directory exist before
        if (!pdfFolder.exists()) pdfFolder.mkdirs()
        if (outFile.exists()) outFile.delete()

        // Write and close the file
        document.save(outFile)
        document.close()

        // Calculate the end of validity date
        val endValidity = Calendar.getInstance().apply {
            // Date
            set(Calendar.YEAR, attestation.outDate.split("/")[2].toInt())
            set(
                Calendar.MONTH,
                attestation.outDate.split("/")[1].toInt().minus(1)
            ) // Minus 1 because month start by 0
            set(Calendar.DAY_OF_MONTH, attestation.outDate.split("/")[0].toInt())

            // Time
            set(Calendar.HOUR_OF_DAY, attestation.outTime.split(":")[0].toInt())
            set(Calendar.MINUTE, attestation.outTime.split(":")[1].toInt())
        }

        // Add it to the database and return it
        return dao.put(
            AttestationPdfDbEntity(
                path = outFile.absolutePath,
                outDateTime = endValidity.time,
                outReasons = attestation.outReasons
            )
        )
    }

    override suspend fun getAttestation(id: Long): AttestationPdfEntity {
        val attestationPdfDb = dao.get(id)

        return AttestationPdfEntity(
            attestationPdfDb.id,
            attestationPdfDb.path!!,
            attestationPdfDb.outDateTime ?: Date(),
            attestationPdfDb.outReasons
        )
    }

    override suspend fun getAttestations(): List<AttestationPdfEntity> {
        return dao.all.map { attestationPdfDb ->
            AttestationPdfEntity(
                attestationPdfDb.id,
                attestationPdfDb.path!!,
                attestationPdfDb.outDateTime ?: Date(),
                attestationPdfDb.outReasons
            )
        }.sortedBy { it.outDateTime }
    }

    override suspend fun deleteAttestation(id: Long) {
        // Try to find the local file and delete it
        dao.get(id).path?.let { path ->
            val localFile = File(path)
            if (localFile.exists()) localFile.delete()
        }

        // Remove it from database
        dao.remove(id)
    }

    /**
     * Write all the informations of the attestions into a PDF Document
     */
    private fun writeAttestationInfos(
        attestation: AttestationEntity,
        generatedDate: Date,
        document: PDDocument,
        page: PDPage
    ) {
        // Create the content stream tht will be used to write in our document
        val contentStream = PDPageContentStream(document, page, true, true, true)

        // Name
        contentStream.writeText("${attestation.surname} ${attestation.name}", 123F, 686F)

        // Birthday and birthplace
        contentStream.writeText(attestation.birthDate, 123F, 661F)
        contentStream.writeText(attestation.birthPlace, 92F, 638F)

        // Address
        contentStream.writeText(
            "${attestation.address}, ${attestation.postalCode} ${attestation.city}",
            134F, 613F
        )

        // Signin address
        contentStream.writeText(attestation.city, 134F, 226F)

        // Out date and time
        contentStream.writeText(attestation.outDate, 92F, 200F)
        contentStream.writeText(attestation.outTime.split(":")[0], 200F, 200F)
        contentStream.writeText(attestation.outTime.split(":")[1], 220F, 200F)

        // Check all the right reason case
        for (outReason in attestation.outReasons) {
            val crossPosition = when (outReason) {
                OutReason.PROFESSIONEL -> 527F
                OutReason.COURSES -> 478F
                OutReason.SOINS -> 436F
                OutReason.FAMILLE -> 400F
                OutReason.SPORT -> 345F
                OutReason.JUDICIAIRE -> 298F
                OutReason.INTERET_GENERAL -> 260F
            }
            contentStream.writeText("x", 76F, crossPosition, 19F)
        }

        // Generated text and time
        contentStream.writeText("Date de création:", 464F, 150F, 7F)
        val generatedFormat = SimpleDateFormat("dd/MM/yyyy 'à' HH'h'mm", Locale.FRANCE)
        contentStream.writeText(generatedFormat.format(generatedDate), 455F, 144F, 7F)

        // Close our content stream
        contentStream.close()
    }

    /**
     * Function used to generate a QrCode data from an attestation
     */
    private fun generateQrCodeData(attestation: AttestationEntity, generatedDate: Date): String {
        // Generate the data for our QrCode
        val generatedFormat = SimpleDateFormat("dd/MM/yyyy 'a' HH'h'mm", Locale.FRANCE)
        val outTimeFormatted =
            attestation.outTime.split(":")[0] + "h" + attestation.outTime.split(":")[1]
        return "Cree le: ${generatedFormat.format(generatedDate)}; " +
                "Nom: ${attestation.name}; " +
                "Prenom: ${attestation.surname}; " +
                "Naissance: ${attestation.birthDate} a ${attestation.birthPlace}; " +
                "Adresse: ${attestation.address} ${attestation.postalCode} ${attestation.city}; " +
                "Sortie: ${attestation.outDate} a ${outTimeFormatted}; " +
                "Motifs: ${attestation.outReasons.joinToString("-") { it.value }}"
    }

    /**
     * Function used to generate a QrCode from a data
     */
    private fun generateQrCode(data: String, size: Int): Bitmap {
        // Tell we don't want margin
        val options = HashMap<EncodeHintType, Int>().apply {
            put(EncodeHintType.MARGIN, 0)
        }
        // Generate the QrCode and encode it to Bitmap
        val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size, options)

        // Generate the bitmap from the bitmatrix
        return bitMatrix.generateBitmap(size)
    }

    /**
     * Write a QrCode into a PDF
     */
    private fun writeQrCode(
        qrCodeData: String,
        document: PDDocument,
        page: PDPage
    ) {
        // Create the content stream tht will be used to write in our document
        val firstPageStream = PDPageContentStream(document, page, true, true)

        // Add to it the bitmap
        firstPageStream.addBitmap(
            document,
            generateQrCode(qrCodeData, 100),
            440F, 160F
        )

        // Close our first content stream
        firstPageStream.close()

        // Create a new page, add it to the document and create the stream
        val secondPage = PDPage()
        document.addPage(secondPage)
        val secondPageStream = PDPageContentStream(document, secondPage, true, true)

        // Add to the page the ArCode
        secondPageStream.addBitmap(
            document,
            generateQrCode(qrCodeData, 300),
            50F, 450F
        )

        // Close our second content stream
        secondPageStream.close()

    }
}