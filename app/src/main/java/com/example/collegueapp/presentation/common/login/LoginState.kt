package com.example.collegueapp.presentation.common.login

import com.example.collegueapp.domain.model.login.UserModel

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserModel) : LoginState()
    object  Error : LoginState()
}