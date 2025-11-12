package com.example.collegueapp.presentation.students.navigation

sealed class StudentRoutes(val route: String) {
    data object Home : StudentRoutes("student_home")
    data object Grades : StudentRoutes("student_grades")
    data object Schedule : StudentRoutes("student_schedule")
    data object Notification : StudentRoutes("student_notifications")
}