package com.dcoxii.apkinstaller.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dcoxii.apkinstaller.installer.InstallWorker
import java.io.File

@Composable
fun ApkListScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues
) {
    val apkDir = File("/data/adb/modules/custom-apk-installer/apks")
    var apks by remember { mutableStateOf(apkDir.listFiles()?.filter { it.name.endsWith(".apk") }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {

        Text("Magisk Module APKs", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        Button(onClick = { apks = apkDir.listFiles()?.toList() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh")
        }

        Spacer(Modifier.height(12.dp))

        Column(Modifier.verticalScroll(rememberScrollState())) {
            apks?.forEach { apk ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            InstallWorker.installViaRoot(listOf(apk))
                        }
                ) {
                    Text(
                        apk.name,
                        Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

