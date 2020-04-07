package com.nivelais.covidout.data.repositories

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.nivelais.covidout.data.entities.Attestation
import com.nivelais.covidout.data.entities.OutReason
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Repository class used to generate PDF
 */
class PdfRepository(
    private val assetManager: AssetManager,
    private val cacheDir: File
) {

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
    suspend fun generate(attestation: Attestation): File {
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
        attestation: Attestation,
        generatedDate: Date,
        document: PDDocument,
        page: PDPage
    ) {
        // Create the content stream tht will be used to write in our document
        val contentStream = PDPageContentStream(document, page, true, true)

        // Name
        contentStream.beginText()
        contentStream.setFont(FONT, 11F)
        contentStream.newLineAtOffset(123F, 686F);
        contentStream.showText("${attestation.surname} ${attestation.name}")
        contentStream.endText()

        // Birthday
        contentStream.beginText()
        contentStream.newLineAtOffset(123F, 661F)
        contentStream.showText(attestation.birthDate)
        contentStream.endText()

        // Birthplace
        contentStream.beginText()
        contentStream.newLineAtOffset(92F, 638F)
        contentStream.showText(attestation.birthPlace)
        contentStream.endText()

        // Address
        contentStream.beginText()
        contentStream.newLineAtOffset(134F, 613F)
        contentStream.showText("${attestation.address}, ${attestation.postalCode} ${attestation.city}")
        contentStream.endText()

        // Address
        contentStream.beginText()
        contentStream.newLineAtOffset(134F, 613F)
        contentStream.showText("${attestation.address}, ${attestation.postalCode} ${attestation.city}")
        contentStream.endText()

        // Signin address
        contentStream.beginText()
        contentStream.newLineAtOffset(134F, 226F)
        contentStream.showText(attestation.city)
        contentStream.endText()

        // Signin address
        contentStream.beginText()
        contentStream.newLineAtOffset(134F, 226F)
        contentStream.showText(attestation.city)
        contentStream.endText()

        // Out date
        contentStream.beginText()
        contentStream.newLineAtOffset(92F, 200F)
        contentStream.showText(attestation.outDate)
        contentStream.endText()

        // Out time hour
        contentStream.beginText()
        contentStream.newLineAtOffset(200F, 200F)
        contentStream.showText(attestation.outTime.split(":")[0])
        contentStream.endText()

        // Out time minute
        contentStream.beginText()
        contentStream.newLineAtOffset(220F, 200F)
        contentStream.showText(attestation.outTime.split(":")[1])
        contentStream.endText()

        // Check the right reason case
        val crossPosition = when (attestation.outReason) {
            OutReason.PROFESSIONEL -> 527F
            OutReason.COURSES -> 478F
            OutReason.SOINS -> 436F
            OutReason.FAMILLE -> 400F
            OutReason.SPORT -> 345F
            OutReason.JUDICIAIRE -> 298F
            OutReason.INTERET_GENERAL -> 260F
        }
        contentStream.beginText()
        contentStream.setFont(FONT, 19F)
        contentStream.newLineAtOffset(76F, crossPosition)
        contentStream.showText("x")
        contentStream.endText()

        // Generated text
        contentStream.beginText()
        contentStream.setFont(FONT, 7F)
        contentStream.newLineAtOffset(464F, 150F)
        contentStream.showText("Date de création:")
        contentStream.endText()

        // Generated time
        val generatedFormat = SimpleDateFormat("dd/MM/yyyy 'à' HH'h'mm", Locale.FRANCE)
        contentStream.beginText()
        contentStream.newLineAtOffset(455F, 144F)
        contentStream.showText(generatedFormat.format(generatedDate))
        contentStream.endText()

        // Close our content stream
        contentStream.close()
    }

    /**
     * Function used to generate a QrCode data from an attestation
     */
    private fun generateQrCodeData(attestation: Attestation, generatedDate: Date): String {
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
                "Motifs: ${attestation.outReason.value}"
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
        val width: Int = bitMatrix.width
        val height: Int = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) BLACK else WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * Write a QrCode into a PDF
     */
    private fun writeQrCode(
        qrCodeData: String,
        document: PDDocument,
        page: PDPage
    ) {
        // Stream that will be used to write our qrcode on the pdf
        var outputStream = ByteArrayOutputStream()

        // Create the content stream tht will be used to write in our document
        val firstContentStream = PDPageContentStream(document, page, true, true)

        // Add the first QrCode on the main page
        val smallQrCode = generateQrCode(qrCodeData, 100)
        smallQrCode.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        val pdfSmallImage = PDImageXObject(
            document, ByteArrayInputStream(outputStream.toByteArray()),
            COSName.DCT_DECODE, smallQrCode.width, smallQrCode.height,
            8,
            PDDeviceRGB.INSTANCE
        );
        firstContentStream.drawImage(pdfSmallImage, 440F, 160F)

        // Close our first content stream
        firstContentStream.close()

        // Create a new page and put the big QrCode
        val secondPage = PDPage()
        document.addPage(secondPage)

        val secondContentStream = PDPageContentStream(document, secondPage, true, true)

        val bigQrCode = generateQrCode(qrCodeData, 300)
        outputStream = ByteArrayOutputStream()
        bigQrCode.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val pdfBigImage = PDImageXObject(
            document, ByteArrayInputStream(outputStream.toByteArray()),
            COSName.DCT_DECODE, bigQrCode.width, bigQrCode.height,
            8,
            PDDeviceRGB.INSTANCE
        );
        secondContentStream.drawImage(pdfBigImage, 50F, 450F)

        // Close our second content stream
        secondContentStream.close()

    }
}