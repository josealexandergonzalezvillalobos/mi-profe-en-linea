package com.dsm.miprofeenlinea.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun bitmapToUri(
    context: Context,
    bitmap: Bitmap
): Uri {

    val file = File(
        context.cacheDir,
        "${UUID.randomUUID()}.jpg"
    )

    FileOutputStream(file).use { outputStream ->

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            outputStream
        )
    }

    return Uri.fromFile(file)
}