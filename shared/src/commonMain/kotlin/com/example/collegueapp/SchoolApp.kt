package com.example.collegueapp

import androidx.compose.runtime.*
import com.example.collegueapp.data.school.SchoolStore
import com.example.collegueapp.presentation.school.LoginScreen
import com.example.collegueapp.presentation.school.SchoolWorkspace
import com.example.collegueapp.ui.theme.SchoolTheme

@Composable
fun SchoolApp(store: SchoolStore) {
    SchoolTheme {
        val account = store.currentAccount()
        if (account == null) LoginScreen(store)
        else key(account.id, account.role) { SchoolWorkspace(account, store, store::logout) }
    }
}
