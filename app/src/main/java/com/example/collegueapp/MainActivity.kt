package com.example.collegueapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.collegueapp.data.local.UserPreferencesDataStorage
import com.example.collegueapp.presentation.common.login.LoginScreen
import com.example.collegueapp.presentation.navigation.AppNavigation
import com.example.collegueapp.ui.theme.CollegueAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStorage: UserPreferencesDataStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegueAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(dataStorage = dataStorage)
                }
            }
        }
    }
}
