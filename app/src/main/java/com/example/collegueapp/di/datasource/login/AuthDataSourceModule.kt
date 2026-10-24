package com.example.collegueapp.di.datasource.login

import com.example.collegueapp.data.remote.datasource.login.AuthDataSourceFirebaseImpl
import com.example.collegueapp.data.remote.datasource.login.AuthDataSourceMockImpl
import com.example.collegueapp.data.remote.datasource.login.AuthDataSourceRetrofitImpl
import com.example.collegueapp.data.remote.services.login.AuthApiServices
import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthDataSourceModule {

    enum class AuthSource {
        MOCK,
        FIREBASE,
        RETROFIT
    }

    private val DATA_SOURCE = AuthSource.MOCK

    @Provides
    @Singleton
    fun provideAuthDataSource(
        authApiServices: AuthApiServices,
        auth: Lazy<FirebaseAuth>,
        firestore: Lazy<FirebaseFirestore>,
    ): AuthDataSource {
        return when (DATA_SOURCE) {
            AuthSource.MOCK -> AuthDataSourceMockImpl()
            AuthSource.FIREBASE -> AuthDataSourceFirebaseImpl(auth = auth.get(), firestore = firestore.get())
            AuthSource.RETROFIT -> AuthDataSourceRetrofitImpl(authApiServices = authApiServices)
        }
    }
}
