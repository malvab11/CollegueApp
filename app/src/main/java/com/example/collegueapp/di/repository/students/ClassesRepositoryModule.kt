package com.example.collegueapp.di.repository.students

import com.example.collegueapp.data.repository.students.ClassesRepositoryImpl
import com.example.collegueapp.domain.datasources.student.classes.ClassesDataSource
import com.example.collegueapp.domain.repository.student.classes.ClassesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClassesRepositoryModule {

    @Provides
    @Singleton
    fun providesClassesRepository(dataSource: ClassesDataSource): ClassesRepository {
        return ClassesRepositoryImpl(dataSource = dataSource)
    }
}