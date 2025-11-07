package com.dcoxii.apkinstaller.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dcoxii.apkinstaller.installer.InstallWorker
import com.dcoxii.apkinstaller.updater.UpdateWorker
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun LogsScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues
) {
    val context = LocalContext.current

    var output by remember { mutableStateOf(InstallWorker.readInstallLog()) }
    var updateAvailable by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Refresh log button
        Button(onClick = { output = InstallWorker.readInstallLog() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Refresh Log") }

        Spacer(Modifier.height(8.dp))

        // ✅ Check for OTA update button
        Button(
            onClick = {
                scope.launch {
                    updateAvailable = UpdateWorker.checkForUpdate(1)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Check for Module Update") }

        // ✅ Show Download button ONLY when update exists
        if (updateAvailable) {
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val data = UpdateWorker.downloadMagiskZip()
                        val file = File(context.getExternalFilesDir(null), "APKInstaller-Magisk.zip")
                        file.writeBytes(data)
                        output += "\n✔ Downloaded update to: ${file.path}\n"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Download Update") }
        }

        Spacer(Modifier.height(12.dp))

        Text("Install Output:", style = MaterialTheme.typography.titleMedium)

        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Text(output, Modifier.padding(8.dp))
        }
    }
}
