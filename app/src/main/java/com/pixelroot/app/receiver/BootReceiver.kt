package com.pixelroot.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pixelroot.app.data.PrefsManager
import com.pixelroot.app.root.RootExecutor
import com.pixelroot.app.root.SuManagerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val prefs = PrefsManager(context)
            if (prefs.startAtBoot) {
                val pendingResult = goAsync()
                val manager = if (prefs.selectedManager == SuManagerType.KERNEL_SU_NEXT.displayName) {
                    SuManagerType.KERNEL_SU_NEXT
                } else {
                    SuManagerType.KERNEL_SU
                }
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RootExecutor.launchRootSession(
                            context = context,
                            manager = manager,
                            prefs = prefs,
                            targetApp = null
                        )
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
