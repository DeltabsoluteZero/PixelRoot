package com.pixelroot.app.data

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pixelroot_prefs", Context.MODE_PRIVATE)

    var startAtBoot: Boolean
        get() = prefs.getBoolean(KEY_START_AT_BOOT, false)
        set(value) = prefs.edit().putBoolean(KEY_START_AT_BOOT, value).apply()

    var autoSoftReboot: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SOFT_REBOOT, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SOFT_REBOOT, value).apply()

    var disableKsuModules: Boolean
        get() = prefs.getBoolean(KEY_DISABLE_KSU_MODULES, false)
        set(value) = prefs.edit().putBoolean(KEY_DISABLE_KSU_MODULES, value).apply()

    var selectedManager: String
        get() = prefs.getString(KEY_SELECTED_MANAGER, "KernelSU") ?: "KernelSU"
        set(value) = prefs.edit().putString(KEY_SELECTED_MANAGER, value).apply()

    var darkMonochrome: Boolean
        get() = prefs.getBoolean(KEY_DARK_MONOCHROME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MONOCHROME, value).apply()

    companion object {
        private const val KEY_START_AT_BOOT = "start_at_boot"
        private const val KEY_AUTO_SOFT_REBOOT = "auto_soft_reboot"
        private const val KEY_DISABLE_KSU_MODULES = "disable_ksu_modules"
        private const val KEY_SELECTED_MANAGER = "selected_manager"
        private const val KEY_DARK_MONOCHROME = "dark_monochrome"
    }
}
