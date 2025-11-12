package com.example.collegueapp.data.remote.datasource.student.classes

import com.example.collegueapp.data.remote.dto.student.classes.ClassesRequestDTO
import com.example.collegueapp.data.remote.dto.student.classes.toDomain
import com.example.collegueapp.data.remote.services.student.classes.ClassesApiServices
import com.example.collegueapp.domain.datasources.student.classes.ClassesDataSource
import com.example.collegueapp.domain.model.student.ClassModel
import javax.inject.Inject

class ClassesDataSourceRetrofitImpl @Inject constructor(
    private val classesApiServices: ClassesApiServices
) : ClassesDataSource {
    override suspend fun getAllClasses(userId: String): List<ClassModel> {
        val response =
            classesApiServices.getAllClasses(request = ClassesRequestDTO(userId = userId))
        return response.map { it -> it.toDomain() }
    }
}