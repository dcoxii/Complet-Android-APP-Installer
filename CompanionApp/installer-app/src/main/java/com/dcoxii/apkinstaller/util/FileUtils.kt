package com.dcoxii.apkinstaller.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

/**
 * Helpers used by installer screens
 */

fun Context.filenameFromUri(uri: Uri): String {
    var name = "file.tmp"
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && cursor.moveToFirst()) name = cursor.getString(idx)
    }
    return name
}

fun Context.copyUriToCache(uri: Uri): File {
    val name = filenameFromUri(uri)
    val file = File(cacheDir, name)
    contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file
}

