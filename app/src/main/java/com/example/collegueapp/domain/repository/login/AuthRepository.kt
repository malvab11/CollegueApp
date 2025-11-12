package com.example.collegueapp.domain.repository.login

import com.example.collegueapp.domain.model.login.UserModel

interface AuthRepository {
    suspend fun authenticateUser(username: String, password: String): Result<UserModel>
}