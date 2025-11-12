package com.example.collegueapp.domain.repository.student.classes

import com.example.collegueapp.domain.model.student.ClassModel

interface ClassesRepository {
    suspend fun getAllClasses(userId: String): Result<List<ClassModel>>
}