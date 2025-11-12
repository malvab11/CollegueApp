package com.example.collegueapp.domain.datasources.student.classes

import com.example.collegueapp.domain.model.student.ClassModel

interface ClassesDataSource {
    suspend fun getAllClasses(userId: String): List<ClassModel>
}