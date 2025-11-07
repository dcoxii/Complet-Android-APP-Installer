package com.dcoxii.apkinstaller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dcoxii.apkinstaller.screen.ApkListScreen
import com.dcoxii.apkinstaller.screen.AdbScreen
import com.dcoxii.apkinstaller.screen.InstallerScreen
import com.dcoxii.apkinstaller.screen.LogsScreen

// Tabs
enum class TabPage(val label: String) {
    APK_LIST("APKs"),
    INSTALLER("Installer"),
    ADB("ADB WiFi"),
    LOGS("Logs")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApkInstallerTheme {
                AppRoot()
            }
        }
    }
}

/**
 * Material 3 Dynamic Theme + Scaffold + Navigation
 */
@Composable
fun AppRoot() {
    var currentTab by remember { mutableStateOf(TabPage.APK_LIST) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                TabPage.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        label = { Text(tab.label) },
                        icon = {}
                    )
                }
            }
        }
    ) { padding ->

        when (currentTab) {
            TabPage.APK_LIST -> ApkListScreen(modifier = Modifier, padding = padding)
            TabPage.INSTALLER -> InstallerScreen(modifier = Modifier, padding = padding)
            TabPage.ADB -> AdbScreen(modifier = Modifier, padding = padding)
            TabPage.LOGS -> LogsScreen(modifier = Modifier, padding = padding)
        }
    }
}

@Composable
fun ApkInstallerTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val darkTheme = isSystemInDarkTheme()

    val colorScheme =
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

