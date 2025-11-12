package com.example.collegueapp.data.remote.dto.login

import com.example.collegueapp.domain.model.login.UserModel

data class LoginResponseDTO (
    val userId: String,
    val role: String,
    val name: String,
)

fun LoginResponseDTO.toDomain() = UserModel(
    userId = userId,
    name = name,
    role = role,
)