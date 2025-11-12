package com.example.collegueapp.di.appModules

import android.content.Context
import com.example.collegueapp.data.local.UserPreferencesDataStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserPreferenceDataStorage(@ApplicationContext context: Context): UserPreferencesDataStorage {
        return UserPreferencesDataStorage(context = context)
    }
}