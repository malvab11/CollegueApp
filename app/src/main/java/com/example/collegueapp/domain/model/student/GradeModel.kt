package com.example.collegueapp.domain.model.student

data class GradeModel(
    val clase: String?,
    val evaluacion: String?,
    val nota: Double? = 0.0,
    val notaMaxima: Double? = 0.0,
)
