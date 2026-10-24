package com.example.collegueapp.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegueapp.data.local.UserPreferencesDataStorage
import com.example.collegueapp.presentation.common.login.LoginScreen
import com.example.collegueapp.presentation.students.navigation.StudentNavigation
import com.example.collegueapp.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(dataStorage: UserPreferencesDataStorage) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.RoleRouter.route
    ) {
        composable(Routes.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.RoleRouter.route) {
                        popUpTo(Routes.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LoadingScreen.route) {
            LoadingScreen()
        }

        composable(Routes.RoleRouter.route) {
            RoleRouterScreen(navController = navController, dataStorage = dataStorage)
        }


        composable(Routes.StudentRoot.route) {
            StudentNavigation(onLogout = {
                coroutineScope.launch {
                    dataStorage.clearUser()
                    navController.navigate(Routes.LoginScreen.route) {
                        popUpTo(Routes.StudentRoot.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            })
        }
//        composable(Routes.TeacherRoot.route) { TeacherNavigation() }
//        composable(Routes.AdminRoot.route) { AdminNavigation() }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Obteniendo información")
        CircularProgressIndicator(color = White) }
}
