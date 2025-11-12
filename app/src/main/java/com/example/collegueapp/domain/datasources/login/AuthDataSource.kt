package com.example.collegueapp.domain.datasources.login

import com.example.collegueapp.domain.model.login.UserModel

interface AuthDataSource {
    suspend fun authenticateUser(username: String, password: String): UserModel
}