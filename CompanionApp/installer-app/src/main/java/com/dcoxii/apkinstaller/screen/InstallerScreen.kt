package com.dcoxii.apkinstaller.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dcoxii.apkinstaller.installer.InstallWorker

@Composable
fun InstallerScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues
) {
    val context = LocalContext.current
    var uris by remember { mutableStateOf(listOf<Uri>()) }
    var logOutput by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0) }
    var installing by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { files ->
        uris = files
    }

    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { picker.launch(arrayOf("*/*")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Pick APK / APKM / XAPK") }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                installing = true
                InstallWorker.installSelected(
                    context,
                    uris,
                    onLog = { logOutput += it + "\n" },
                    onProgress = { progress = it },
                    onDone = { installing = false }
                )
            }
        ) { Text("Install") }

        if (installing) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = progress / 100f)
            Text("Installing... ${progress}%")
        }

        Spacer(Modifier.height(12.dp))

        Text("Install Output:", style = MaterialTheme.typography.titleMedium)
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.Black.copy(alpha = 0.1f))
                .verticalScroll(rememberScrollState())
        ) {
            Text(logOutput, Modifier.padding(8.dp))
        }
    }
}

