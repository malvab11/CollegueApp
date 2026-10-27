package com.example.collegueapp.platform

import androidx.compose.runtime.Composable
import platform.Foundation.NSUserDefaults

class IosSchoolStorage : SchoolStorage {
    private val defaults = NSUserDefaults.standardUserDefaults
    override fun get(key: String): String? = defaults.stringForKey("collegue_$key")
    override fun put(key: String, value: String?) {
        if (value == null) defaults.removeObjectForKey("collegue_$key") else defaults.setObject(value, "collegue_$key")
    }
}
@Composable
actual fun SchoolBackHandler(enabled: Boolean, onBack: () -> Unit) { /* Navigation is provided by the shared toolbar on iOS. */ }
