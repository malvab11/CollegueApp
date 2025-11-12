package com.example.collegueapp.presentation.students.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegueapp.domain.usecase.student.GetAllClassesUseCase
import com.example.collegueapp.presentation.students.screens.home.StudentHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val getAllClassesUseCase: GetAllClassesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentHomeUiState>(StudentHomeUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadAllClasses(userId: String) {
        viewModelScope.launch {
            _uiState.value = StudentHomeUiState.Loading

            val result = getAllClassesUseCase(userId = userId)

            result.fold(
                onSuccess = {
                    _uiState.value = StudentHomeUiState.Success(it)
                },
                onFailure = {
                    _uiState.value = StudentHomeUiState.Error
                    _uiEvent.emit("Error al obtener las clases, vuelva a intentar")
                }
            )
        }
    }

}