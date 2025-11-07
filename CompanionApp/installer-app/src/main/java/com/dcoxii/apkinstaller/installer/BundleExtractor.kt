package com.dcoxii.apkinstaller.installer

import android.content.Context
import com.dcoxii.apkinstaller.util.copyUriToCache
import java.io.File

object BundleExtractor {

    private const val EXTRACT_PATH = "/data/local/tmp/apkinstall_splits"

    fun extractBundle(context: Context, bundleUri: android.net.Uri): List<File> {

        // Copy bundle into cache
        val file = context.copyUriToCache(bundleUri)
        shell("rm -rf $EXTRACT_PATH")
        shell("mkdir -p $EXTRACT_PATH")

        // unzip .apkm/.xapk -> /tmp/apkinstall_splits
        shell("unzip -o \"${file.path}\" -d $EXTRACT_PATH")

        return File(EXTRACT_PATH)
            .listFiles()
            ?.filter { it.name.endsWith(".apk") }
            ?: emptyList()
    }

    private fun shell(cmd: String) =
        Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor()
}

