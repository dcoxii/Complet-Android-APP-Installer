package com.dcoxii.apkinstaller.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dcoxii.apkinstaller.installer.InstallWorker
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanContract

@Composable
fun AdbScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues
) {
    val ctx = LocalContext.current

    var ip by remember { mutableStateOf("") }
    var pairingCode by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    val qrScanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            status = "QR scanned"
            val qr = result.contents

            val ipMatch = Regex("A:(.*?);").find(qr)?.groupValues?.get(1)
            val codeMatch = Regex("P:(.*?);").find(qr)?.groupValues?.get(1)

            if (ipMatch != null && codeMatch != null) {
                ip = ipMatch
                pairingCode = codeMatch
                status = "Parsed: $ip / code: $pairingCode"
                InstallWorker.adbPair(ipMatch, codeMatch)
            } else {
                status = "Invalid ADB QR code"
            }
        }
    }

    Column(
        modifier = modifier
            .padding(padding)
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = ip,
            onValueChange = { ip = it },
            label = { Text("ADB Host (ip:port)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pairingCode,
            onValueChange = { pairingCode = it },
            label = { Text("Pairing code (Android 12+)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { InstallWorker.adbPair(ip, pairingCode) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pair via ADB")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                qrScanner.launch(
                    ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        .setPrompt("Scan ADB pairing QR")
                        .setBeepEnabled(false)
                        .setOrientationLocked(false)
                )
            }
        ) { Text("Scan QR Code") }

        Spacer(Modifier.height(12.dp))
        Text(status)
    }
}

