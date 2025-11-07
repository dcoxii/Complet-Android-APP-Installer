package com.dcoxii.apkinstaller.updater

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object UpdateWorker {

    private const val UPDATE_URL =
        "https://raw.githubusercontent.com/dcoxii/Complet-Android-APP-Installer/main/update.json"

    suspend fun checkForUpdate(currentVersion: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(URL(UPDATE_URL).readText())
            val remoteVersion = json.getInt("versionCode")
            return@withContext (remoteVersion > currentVersion)
        } catch (_: Exception) {
            return@withContext false
        }
    }

    suspend fun downloadMagiskZip(): ByteArray = withContext(Dispatchers.IO) {
        val json = JSONObject(URL(UPDATE_URL).readText())
        val url = json.getString("zipUrl")
        return@withContext URL(url).readBytes()
    }
}
