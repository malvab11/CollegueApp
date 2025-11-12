package com.example.collegueapp.di.datasource.students

import com.example.collegueapp.data.remote.datasource.student.classes.ClassesDataSourceFirebaseImpl
import com.example.collegueapp.data.remote.datasource.student.classes.ClassesDataSourceMockImpl
import com.example.collegueapp.data.remote.datasource.student.classes.ClassesDataSourceRetrofitImpl
import com.example.collegueapp.data.remote.services.student.classes.ClassesApiServices
import com.example.collegueapp.domain.datasources.student.classes.ClassesDataSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClassesDataSourceModule {

    enum class ClassDataSource {
        MOCK,
        FIREBASE,
        RETROFIT
    }

    private val DATA_SOURCE = ClassDataSource.MOCK

    @Provides
    @Singleton
    fun provideClassesDataSource(
        firestore: FirebaseFirestore,
        classesApiServices: ClassesApiServices,
    ): ClassesDataSource {
        return when (DATA_SOURCE) {
            ClassDataSource.MOCK -> ClassesDataSourceMockImpl()
            ClassDataSource.FIREBASE -> ClassesDataSourceFirebaseImpl(firestore = firestore)
            ClassDataSource.RETROFIT -> ClassesDataSourceRetrofitImpl(classesApiServices = classesApiServices)
        }
    }
}