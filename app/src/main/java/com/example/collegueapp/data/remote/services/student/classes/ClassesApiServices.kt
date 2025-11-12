package com.example.collegueapp.data.remote.services.student.classes

import com.example.collegueapp.data.remote.dto.student.classes.ClassesRequestDTO
import com.example.collegueapp.data.remote.dto.student.classes.ClassesResponseDTO

interface ClassesApiServices {
    suspend fun getAllClasses(request: ClassesRequestDTO): List<ClassesResponseDTO>
}