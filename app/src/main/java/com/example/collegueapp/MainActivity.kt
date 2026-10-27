package com.example.collegueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.collegueapp.data.school.SchoolStore
import com.example.collegueapp.platform.AndroidSchoolStorage

class MainActivity : ComponentActivity() {
    lateinit var schoolStore: SchoolStore
        private set
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        schoolStore = SchoolStore(AndroidSchoolStorage(applicationContext))
        setContent { SchoolApp(schoolStore) }
    }
}
