package com.nivelais.covidout.data

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.common.BitMatrix
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Generate a QrCode bitmap from a bitmatrix
 */
fun BitMatrix.generateBitmap(size: Int): Bitmap {
    // Generate the bitmap from the bitmatrix
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        val offset = y * size
        for (x in 0 until size) {
            pixels[offset + x] =
                if (this.get(x, y)) Color.BLACK else Color.WHITE
        }
    }

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
    return bitmap
}


/**
 * Extensions helping us to write text to a pdf
 */
fun PDPageContentStream.writeText(text: String, posX: Float, posY: Float, textSize: Float = 11F) {
    this.apply {
        beginText()
        setFont(PDType1Font.HELVETICA, textSize)
        newLineAtOffset(posX, posY);
        showText(text)
        endText()
    }
}

fun PDPageContentStream.addBitmap(document: PDDocument, bitmap: Bitmap, posX: Float, posY: Float) {
    // Stream that will be used to write our bitmap on the pdf
    val outputStream = ByteArrayOutputStream()

    // Compress the bitmap to the stream
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

    // Create the image that we will be inserting
    val pdfImage = PDImageXObject(
        document,
        ByteArrayInputStream(outputStream.toByteArray()),
        COSName.DCT_DECODE,
        bitmap.width,
        bitmap.height,
        8,
        PDDeviceRGB.INSTANCE
    );

    // Draw the image on the pdf
    this.drawImage(pdfImage, posX, posY)
}