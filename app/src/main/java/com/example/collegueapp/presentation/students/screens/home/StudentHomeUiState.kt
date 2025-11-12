package com.example.collegueapp.presentation.students.screens.home

import com.example.collegueapp.domain.model.student.ClassModel

sealed class StudentHomeUiState {
    object Idle : StudentHomeUiState()
    object Loading : StudentHomeUiState()
    data class Success(val classes: List<ClassModel>) : StudentHomeUiState()
    object Error : StudentHomeUiState()
}