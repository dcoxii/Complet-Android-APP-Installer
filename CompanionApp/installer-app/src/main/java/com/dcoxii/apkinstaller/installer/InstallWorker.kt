package com.dcoxii.apkinstaller.installer

import android.content.Context
import android.net.Uri
import com.dcoxii.apkinstaller.util.copyUriToCache
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object InstallWorker {

    private const val LOG_PATH = "/data/local/tmp/magisk-apkinstall.log"  
    
    fun adbPair(ip: String, code: String) {
    root("adb pair $ip $code")
}

    /**
     * Single or split APK installation, with progress and logging
     */
    fun installSelected(
        context: Context,
        uris: List<Uri>,
        onLog: (String) -> Unit,
        onProgress: (Int) -> Unit,
        onDone: () -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {

        streamLogcat(onLog, onProgress)

        val apkFiles = uris.flatMap { uri ->
            val name = context.filenameFromUri(uri)
            if (name.endsWith(".apkm") || name.endsWith(".xapk")) {
                BundleExtractor.extractBundle(context, uri)
            } else listOf(context.copyUriToCache(uri))
        }

        installViaRoot(apkFiles)
        onDone()
    }


    fun installViaRoot(files: List<File>) {
        if (files.size == 1)
            root("pm install -r -g \"${files[0].path}\"")
        else {
            val group = files.joinToString(" ") { "\"${it.path}\"" }
            root("pm install-multiple -r -g $group")
        }
    }


    fun installViaAdb(ip: String) {
        root("adb connect $ip")
        root("adb install-multiple -r -g /data/local/tmp/*.apk")
    }


    /**
     * read install log into LogsScreen
     */
    fun readInstallLog(): String =
        try {
            val p = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat $LOG_PATH"))
            p.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            "log read error: ${e.message}"
        }


    /**
     * Live logcat streaming and progress extraction
     */
    private fun streamLogcat(onLog: (String) -> Unit, onProgress: (Int) -> Unit) {
        Thread {
            try {
                val p = Runtime.getRuntime().exec("logcat -s PackageInstaller:I")
                val reader = BufferedReader(InputStreamReader(p.inputStream))

                while (true) {
                    val line = reader.readLine() ?: break
                    onLog(line)

                    val progress = Regex("progress: (\\d+)%").find(line)?.groupValues?.getOrNull(1)
                    if (progress != null) onProgress(progress.toInt())
                }

            } catch (e: Exception) {
                onLog("logcat error: ${e.message}")
            }
        }.start()
    }


    private fun root(cmd: String) {
        Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor()
    }
}

