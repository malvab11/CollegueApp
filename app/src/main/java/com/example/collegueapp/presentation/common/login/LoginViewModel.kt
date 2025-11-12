package com.example.collegueapp.presentation.common.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegueapp.data.local.UserPreferencesDataStorage
import com.example.collegueapp.domain.usecase.login.AuthenticateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticateUserUseCase: AuthenticateUserUseCase,
    private val userPreferencesDataStorage: UserPreferencesDataStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading

            val result = authenticateUserUseCase(username, password)

            result.fold(
                onSuccess = { user ->
                    _uiState.value = LoginState.Success(user)
                    userPreferencesDataStorage.saveUser(name = user.name, role = user.role)
                    _uiEvent.emit("Bienvenido ${user.name}")
                },
                onFailure = {
                    _uiState.value = LoginState.Error
                    _uiEvent.emit("Error al Iniciar Sesión, vuelva a intentar")
                }
            )
        }
    }

}