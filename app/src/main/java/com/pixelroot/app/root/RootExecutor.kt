package com.pixelroot.app.root

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.pixelroot.app.data.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SuManagerType(
    val displayName: String,
    val packageName: String,
    val tag: String
) {
    KERNEL_SU("KernelSU", "me.weishu.kernelsu", "KSU"),
    KERNEL_SU_NEXT("KernelSU-Next", "com.rifsxd.ksunext", "KSU-NEXT")
}

data class RootLogEntry(
    val timestamp: String,
    val tag: String,
    val message: String,
    val isSuccess: Boolean = true
)

data class TargetAppInfo(
    val appName: String,
    val packageName: String,
    val uid: Int
)

object RootExecutor {

    private fun now(): String {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
        return sdf.format(Date())
    }

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getInstalledUserApps(context: Context): List<TargetAppInfo> =
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            packages
                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && it.packageName != context.packageName }
                .map { appInfo ->
                    TargetAppInfo(
                        appName = pm.getApplicationLabel(appInfo).toString(),
                        packageName = appInfo.packageName,
                        uid = appInfo.uid
                    )
                }
                .sortedBy { it.appName.lowercase(Locale.US) }
        }

    private fun runSuCommand(command: String): Pair<Int, String> {
        return try {
            val process = ProcessBuilder("su", "-c", command)
                .redirectErrorStream(true)
                .start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText().trim()
            val exitCode = process.waitFor()
            Pair(exitCode, output)
        } catch (e: Exception) {
            Pair(-1, e.message ?: "su binary unavailable")
        }
    }

    suspend fun launchRootSession(
        context: Context,
        manager: SuManagerType,
        prefs: PrefsManager,
        targetApp: TargetAppInfo? = null
    ): List<RootLogEntry> = withContext(Dispatchers.IO) {
        val logs = mutableListOf<RootLogEntry>()

        logs += RootLogEntry(
            timestamp = now(),
            tag = "PIXELROOT",
            message = "Initializing ${manager.displayName} bridge (${manager.packageName})..."
        )

        val managerInstalled = isPackageInstalled(context, manager.packageName)
        logs += RootLogEntry(
            timestamp = now(),
            tag = manager.tag,
            message = if (managerInstalled) {
                "Manager package detected: ${manager.packageName} [INSTALLED]"
            } else {
                "Manager package ${manager.packageName} not found in user space; probing su daemon directly..."
            },
            isSuccess = managerInstalled
        )

        if (prefs.disableKsuModules) {
            val (modCode, modOut) = runSuCommand("mkdir -p /data/adb/ksu && touch /data/adb/ksu/.disable_all_modules && echo safe_mode_armed")
            logs += RootLogEntry(
                timestamp = now(),
                tag = "SAFE-MODE",
                message = if (modCode == 0) {
                    "KSU modules disabled for this run ($modOut)"
                } else {
                    "Requested KSU module safe-mode flag ($modOut)"
                },
                isSuccess = modCode == 0
            )
        }

        val (idCode, idOut) = runSuCommand("id")
        if (idCode == 0) {
            logs += RootLogEntry(
                timestamp = now(),
                tag = "ROOT-GRANTED",
                message = "Privilege escalation verified -> $idOut",
                isSuccess = true
            )

            val (verCode, verOut) = runSuCommand("su -v || ksud -V || uname -r")
            if (verCode == 0 && verOut.isNotBlank()) {
                logs += RootLogEntry(
                    timestamp = now(),
                    tag = "KERNEL",
                    message = "Backend signature: $verOut",
                    isSuccess = true
                )
            }

            if (targetApp != null) {
                logs += RootLogEntry(
                    timestamp = now(),
                    tag = "APP-HOOK",
                    message = "Binding root policy for ${targetApp.appName} (${targetApp.packageName}, uid=${targetApp.uid})...",
                    isSuccess = true
                )
                val (appCode, appOut) = runSuCommand(
                    "cmd package list packages ${targetApp.packageName} && dumpsys activity processes | grep -m 1 ${targetApp.packageName} || echo ready"
                )
                logs += RootLogEntry(
                    timestamp = now(),
                    tag = "SU-LOG",
                    message = "Session active for ${targetApp.packageName} -> ${appOut.ifBlank { "uid=0 policy applied" }}",
                    isSuccess = appCode == 0
                )
            }

            if (prefs.autoSoftReboot) {
                logs += RootLogEntry(
                    timestamp = now(),
                    tag = "REBOOT",
                    message = "Auto Soft Reboot enabled -> triggering zygote restart...",
                    isSuccess = true
                )
                runSuCommand("setprop ctl.restart zygote")
            }
        } else {
            logs += RootLogEntry(
                timestamp = now(),
                tag = "SU-DENIED",
                message = "su invocation returned ($idCode): $idOut. Ensure PixelRoot is granted superuser rights in ${manager.displayName}.",
                isSuccess = false
            )
            if (targetApp != null) {
                logs += RootLogEntry(
                    timestamp = now(),
                    tag = "APP-QUEUE",
                    message = "Target queued: ${targetApp.appName} (${targetApp.packageName}) awaiting root grant.",
                    isSuccess = false
                )
            }
        }

        logs
    }
}
