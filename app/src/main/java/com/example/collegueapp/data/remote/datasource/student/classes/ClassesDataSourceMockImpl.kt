package com.example.collegueapp.data.remote.datasource.student.classes

import com.example.collegueapp.domain.datasources.student.classes.ClassesDataSource
import com.example.collegueapp.domain.model.login.UserModel
import com.example.collegueapp.domain.model.student.ClassModel
import javax.inject.Inject

class ClassesDataSourceMockImpl @Inject constructor() : ClassesDataSource {

    private val mockClasses = listOf(
        ClassModel(
            clase = "Matemáticas",
            horas = "10:00 AM - 12:15 PM",
            profesor = "Henry Aguilar",
            tipoAula = "Salón",
            aula = "101",
        ),
        ClassModel(
            clase = "Historia",
            horas = "08:00 AM - 09:00 AM",
            profesor = "Claudia Villajulca Aliaga",
            tipoAula = "Salón",
            aula = "101",
        ),
        ClassModel(
            clase = "Educación Física",
            horas = "06:00 AM - 08:00 AM",
            profesor = "Mirko Arana",
            tipoAula = "Patio",
            aula = "",
        ),
        ClassModel(
            clase = "Educación Física",
            horas = "06:00 AM - 08:00 AM",
            profesor = "Mirko Arana",
            tipoAula = "Patio",
            aula = "",
        ),
        ClassModel(
            clase = "Educación Física",
            horas = "06:00 AM - 08:00 AM",
            profesor = "Mirko Arana",
            tipoAula = "Patio",
            aula = "",
        ),
    )

    override suspend fun getAllClasses(userId: String): List<ClassModel> {
        return mockClasses
    }

}