package com.nivelais.covidout.data.repositories

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.nivelais.covidout.common.entities.AttestationEntity
import com.nivelais.covidout.common.entities.OutReason
import com.nivelais.covidout.common.repositories.PdfRepository
import com.nivelais.covidout.data.addBitmap
import com.nivelais.covidout.data.generateBitmap
import com.nivelais.covidout.data.writeText
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Repository class used to generate PDF
 */
class PdfRepositoryImpl(
    private val assetManager: AssetManager,
    private val cacheDir: File
) : PdfRepository {

    companion object {
        // Template file to load from the assets
        private const val TEMPLATE_NAME = "attestation-deplacement.pdf"

        // Font used in the pdf
        private val FONT = PDType1Font.HELVETICA

        // Color to the qr code
        private const val WHITE = -0x1
        private const val BLACK = -0x1000000
    }


    /**
     * Generate a PDF File from an attestation object
     */
    override suspend fun generate(attestation: AttestationEntity): File {
        // Get the template document and first page
        val document = PDDocument.load(assetManager.open(TEMPLATE_NAME))
        val page = document.getPage(0)

        val generatedDate = Date();

        // Add some text
        writeAttestationInfos(attestation, generatedDate, document, page);

        // Generate the QrCode
        val qrCodeData = generateQrCodeData(attestation, generatedDate)

        // Add the QrCode to the PDF
        writeQrCode(qrCodeData, document, page)

        // Generate an output file
        val outFile = File(cacheDir, "tmp_attestations.pdf")

        // Write and close the file
        document.save(outFile)
        document.close()

        // Return it
        return outFile
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
        val contentStream = PDPageContentStream(document, page, true, true)

        // Name
        contentStream.writeText("${attestation.surname} ${attestation.name}", 123F, 686F)

        // Birthday
        contentStream.writeText(attestation.birthDate, 123F, 661F)

        // Birthplace
        contentStream.writeText(attestation.birthPlace, 92F, 638F)

        // Address
        contentStream.writeText(
            "${attestation.address}, ${attestation.postalCode} ${attestation.city}",
            134F, 613F
        )

        // Signin address
        contentStream.writeText(attestation.city, 134F, 226F)

        // Out date
        contentStream.writeText(attestation.outDate, 92F, 200F)

        // Out time hour
        contentStream.writeText(attestation.outTime.split(":")[0], 200F, 200F)

        // Out time minute
        contentStream.writeText(attestation.outTime.split(":")[1], 220F, 200F)

        // Check the right reason case
        val crossPosition = when (attestation.outReason) {
            OutReason.PROFESSIONEL -> 527F
            OutReason.COURSES -> 478F
            OutReason.SOINS -> 436F
            OutReason.FAMILLE -> 400F
            OutReason.SPORT -> 345F
            OutReason.JUDICIAIRE -> 298F
            OutReason.INTERET_GENERAL -> 260F
            else -> 478F
        }
        contentStream.writeText("x", 76F, crossPosition, 19F)
        contentStream.writeText("x", 76F, crossPosition, 19F)

        // Generated text
        contentStream.writeText("Date de création:", 464F, 150F, 7F)

        // Generated time
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
        return "Cree le: ${generatedFormat.format(generatedDate)};" +
                "Nom: ${attestation.name};" +
                "Prenom: ${attestation.surname};" +
                "Naissance: ${attestation.birthDate} a ${attestation.birthPlace};" +
                "Adresse: ${attestation.address} ${attestation.postalCode} ${attestation.city};" +
                "Sortie: ${attestation.outDate} a ${outTimeFormatted};" +
                "Motifs: ${attestation.outReason?.value}"
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