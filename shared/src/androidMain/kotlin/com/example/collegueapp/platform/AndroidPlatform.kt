package com.example.collegueapp.platform

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

class AndroidSchoolStorage(context: Context) : SchoolStorage {
    private val prefs =
        context.applicationContext.getSharedPreferences("collegue_school", Context.MODE_PRIVATE)

    override fun get(key: String): String? = prefs.getString(key, null)
    override fun put(key: String, value: String?) {
        prefs.edit().apply { if (value == null) remove(key) else putString(key, value) }.apply()
    }
}

@Composable
actual fun SchoolBackHandler(enabled: Boolean, onBack: () -> Unit) = BackHandler(enabled, onBack)
