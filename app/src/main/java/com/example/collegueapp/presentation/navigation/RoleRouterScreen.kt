package com.example.collegueapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.collegueapp.data.local.UserPreferencesDataStorage

@Composable
fun RoleRouterScreen(navController: NavController, dataStorage: UserPreferencesDataStorage) {

    val role by dataStorage.userRole.collectAsState(initial = "loading")

//    LaunchedEffect(Unit) {
//        dataStorage.clearUser()
//    }

    LaunchedEffect(role) {
        when (role) {
            "loading" -> {
                navController.navigate(Routes.LoadingScreen.route) { popUpTo(0) }
            }

            "admin" -> navController.navigate(Routes.AdminRoot.route) { popUpTo(0) }
            "profesor" -> navController.navigate(Routes.TeacherRoot.route) { popUpTo(0) }
            "estudiante" -> navController.navigate(Routes.StudentRoot.route) { popUpTo(0) }
            null, "" -> navController.navigate(Routes.LoginScreen.route) { popUpTo(0) }
        }
    }

}