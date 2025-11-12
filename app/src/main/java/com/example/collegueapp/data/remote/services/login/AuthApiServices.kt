package com.example.collegueapp.data.remote.services.login

import com.example.collegueapp.data.remote.dto.login.LoginRequestDTO
import com.example.collegueapp.data.remote.dto.login.LoginResponseDTO

interface AuthApiServices {
    suspend fun authenticateUser(request: LoginRequestDTO) : LoginResponseDTO
}