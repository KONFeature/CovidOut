package com.nivelais.covidout.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.FileProvider
import com.google.zxing.common.BitMatrix
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.ArrayList

/**
 * Open a file via an intent
 */
fun File.openViaIntent(context: Context) {
    // Create the uri and the intent to open the attestations
    val uri =
        FileProvider.getUriForFile(
            context,
            "com.nivelais.covidout.provider",
            this
        )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
    }

    // Approve all the potential openner of the file
    val resInfoList = context.packageManager?.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    for (resolveInfo in resInfoList ?: ArrayList()) {
        val packageName = resolveInfo.activityInfo.packageName
        context.grantUriPermission(
            packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    // Launch the openning of the attestations
    context.startActivity(intent)
}