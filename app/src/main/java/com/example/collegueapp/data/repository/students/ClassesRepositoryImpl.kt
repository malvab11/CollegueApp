package com.example.collegueapp.data.repository.students

import com.example.collegueapp.domain.datasources.student.classes.ClassesDataSource
import com.example.collegueapp.domain.model.student.ClassModel
import com.example.collegueapp.domain.repository.student.classes.ClassesRepository
import javax.inject.Inject

class ClassesRepositoryImpl @Inject constructor(
    private val dataSource: ClassesDataSource
) : ClassesRepository {
    override suspend fun getAllClasses(userId: String): Result<List<ClassModel>> {
        return try {
            val response = dataSource.getAllClasses(userId = userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure<List<ClassModel>>(e)
        }
    }
}