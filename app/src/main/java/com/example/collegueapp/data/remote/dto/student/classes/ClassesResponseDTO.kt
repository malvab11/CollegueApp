package com.example.collegueapp.data.remote.dto.student.classes

import com.example.collegueapp.domain.model.student.ClassModel

data class ClassesResponseDTO(
    val clase: String,
    val horas: String,
    val profesor: String,
    val tipoAula: String,
    val aula: String
)

fun ClassesResponseDTO.toDomain() = ClassModel(
    clase = clase,
    horas = horas,
    profesor = profesor,
    tipoAula = tipoAula,
    aula = aula
)