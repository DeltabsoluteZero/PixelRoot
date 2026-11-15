package com.pixelroot.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pixelroot.app.data.PrefsManager
import com.pixelroot.app.ui.screens.PixelRootScreen
import com.pixelroot.app.ui.theme.PixelRootTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PrefsManager(this)

        setContent {
            var isDarkMonochrome by remember { mutableStateOf(prefs.darkMonochrome) }

            LaunchedEffect(isDarkMonochrome) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkMonochrome) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (isDarkMonochrome) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                )
            }

            PixelRootTheme(darkTheme = isDarkMonochrome) {
                PixelRootScreen(
                    prefs = prefs,
                    isDarkMonochrome = isDarkMonochrome,
                    onToggleDarkMonochrome = { enabled ->
                        isDarkMonochrome = enabled
                        prefs.darkMonochrome = enabled
                    }
                )
            }
        }
    }
}
