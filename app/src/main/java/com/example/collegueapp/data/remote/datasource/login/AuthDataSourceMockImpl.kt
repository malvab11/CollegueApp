package com.example.collegueapp.data.remote.datasource.login

import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.example.collegueapp.domain.model.login.UserModel
import javax.inject.Inject

class AuthDataSourceMockImpl @Inject constructor() : AuthDataSource {

    private val mockUsers = listOf(
        Triple("admin", "123456", "admin"),
        Triple("profesor", "123456", "profesor"),
        Triple("estudiante", "123456", "estudiante")
    )

    override suspend fun authenticateUser(
        username: String,
        password: String
    ): UserModel {

        val match = mockUsers.find { it.first == username && it.second == password }
            ?: throw Exception("Usuario o contraseña incorrectos")

        return UserModel(
            userId = "mock_${match.first}",
            name = match.first.replaceFirstChar { it.uppercase() },
            role = match.third
        )
    }
}