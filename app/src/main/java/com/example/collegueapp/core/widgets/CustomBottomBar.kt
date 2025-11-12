package com.example.collegueapp.core.widgets

import android.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.collegueapp.presentation.navigation.NavigationDataClass
import com.example.collegueapp.ui.theme.DarkText
import com.example.collegueapp.ui.theme.White


@Composable
fun CustomBottomBar(
    navController: NavHostController,
    items: List<NavigationDataClass>,
    startRoute: String
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(startRoute)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, null) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,   // Icono seleccionado
                    selectedTextColor = MaterialTheme.colorScheme.primary,   // Texto seleccionado

                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Icono normal
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant, // Texto normal

                    indicatorColor = MaterialTheme.colorScheme.background.copy(0.0f)
                )
            )
        }
    }
}