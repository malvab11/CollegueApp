package com.example.collegueapp.domain.usecase.login

import com.example.collegueapp.domain.model.login.UserModel
import com.example.collegueapp.domain.repository.login.AuthRepository
import javax.inject.Inject

class AuthenticateUserUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(username: String, password: String): Result<UserModel> =
        repository.authenticateUser(username = username, password = password)
}