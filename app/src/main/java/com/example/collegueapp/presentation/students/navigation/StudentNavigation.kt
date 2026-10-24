package com.example.collegueapp.presentation.students.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.collegueapp.core.widgets.CustomBottomBar
import com.example.collegueapp.presentation.navigation.NavigationDataClass
import com.example.collegueapp.presentation.students.screens.grades.StudentGradesScreen
import com.example.collegueapp.presentation.students.screens.home.StudentHomeScreen
import com.example.collegueapp.presentation.students.screens.notifications.StudentNotificationScreen
import com.example.collegueapp.presentation.students.screens.schedule.StudentScheduleScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentNavigation(onLogout: () -> Unit) {

    val studentNavController = rememberNavController()

    val currentRoute = studentNavController.currentBackStackEntryAsState().value?.destination?.route

    val menuOptions = listOf<NavigationDataClass>(
        NavigationDataClass("Inicio", StudentRoutes.Home.route, Icons.Default.BarChart),
        NavigationDataClass("Notas", StudentRoutes.Grades.route, Icons.Default.School),
        NavigationDataClass("Horario", StudentRoutes.Schedule.route, Icons.Default.Schedule),
        NavigationDataClass("Mensajes", StudentRoutes.Notification.route, Icons.Default.Task)
    )

    Scaffold(bottomBar = {
        CustomBottomBar(
            navController = studentNavController,
            items = menuOptions,
            startRoute = StudentRoutes.Home.route
        )
    }, topBar = {
        if (currentRoute != StudentRoutes.Home.route) {
            TopAppBar(title = {
                Text(
                    when (currentRoute) {
                        StudentRoutes.Grades.route -> "Mis Notas"
                        StudentRoutes.Schedule.route -> "Mi Horario"
                        StudentRoutes.Notification.route -> "Mensajes"
                        else -> "App"
                    }
                , modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            })
        }

    }) { innerPadding ->
        NavHost(
            navController = studentNavController,
            startDestination = StudentRoutes.Home.route
        ) {
            composable(StudentRoutes.Home.route) {
                StudentHomeScreen(
                    paddingValues = innerPadding,
                    horizontalPadding = 12.dp,
                    userId = "x",
                    onLogout = onLogout
                )
            }
            composable(StudentRoutes.Grades.route) { StudentGradesScreen(paddingValues = innerPadding, horizontalPadding= 12.dp) }
            composable(StudentRoutes.Schedule.route) { StudentScheduleScreen(paddingValues = innerPadding , horizontalPadding= 12.dp) }
            composable(StudentRoutes.Notification.route) { StudentNotificationScreen(paddingValues = innerPadding , horizontalPadding= 12.dp) }
        }
    }
}
