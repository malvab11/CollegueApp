package com.example.collegueapp.data.repository.login

import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.example.collegueapp.domain.model.login.UserModel
import com.example.collegueapp.domain.repository.login.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthDataSource
): AuthRepository {
    override suspend fun authenticateUser(
        username: String,
        password: String
    ): Result<UserModel> {
        return try {
            val user = remoteDataSource.authenticateUser(username, password)
            Result.success(user)
        }catch (e: Exception){
            Result.failure<UserModel>(e)
        }
    }
}