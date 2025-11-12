package com.example.collegueapp.data.remote.datasource.login

import com.example.collegueapp.data.remote.dto.login.LoginRequestDTO
import com.example.collegueapp.data.remote.dto.login.toDomain
import com.example.collegueapp.data.remote.services.login.AuthApiServices
import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.example.collegueapp.domain.model.login.UserModel
import javax.inject.Inject

class AuthDataSourceRetrofitImpl @Inject constructor(
    private val authApiServices: AuthApiServices
) : AuthDataSource {
    override suspend fun authenticateUser(
        username: String,
        password: String
    ): UserModel {
        val response = authApiServices.authenticateUser(LoginRequestDTO(username, password))
        return response.toDomain()
    }
}