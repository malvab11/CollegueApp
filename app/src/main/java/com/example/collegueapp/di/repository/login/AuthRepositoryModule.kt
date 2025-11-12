package com.example.collegueapp.di.repository.login

import com.example.collegueapp.data.repository.login.AuthRepositoryImpl
import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.example.collegueapp.domain.repository.login.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {

    @Provides
    @Singleton
    fun providesAuthRepository(dataSource: AuthDataSource): AuthRepository {
        return AuthRepositoryImpl(remoteDataSource = dataSource)
    }
}