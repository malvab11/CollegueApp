package com.example.collegueapp.presentation.navigation

sealed class Routes(val route: String){
    data object LoginScreen : Routes("login_screen")
    data object LoadingScreen : Routes("loading_screen")
    data object RoleRouter : Routes("role_router")

    data object StudentRoot: Routes("student_root")
    data object TeacherRoot: Routes("teacher_root")
    data object AdminRoot: Routes("admin_root")
}
