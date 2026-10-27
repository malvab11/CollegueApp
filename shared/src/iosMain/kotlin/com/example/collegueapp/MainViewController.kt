package com.example.collegueapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.collegueapp.data.school.SchoolStore
import com.example.collegueapp.platform.IosSchoolStorage

fun MainViewController() = ComposeUIViewController { SchoolApp(iosSchoolStore) }
private val iosSchoolStore by lazy { SchoolStore(IosSchoolStorage()) }
