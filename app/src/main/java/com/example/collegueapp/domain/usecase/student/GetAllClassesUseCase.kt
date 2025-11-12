package com.example.collegueapp.domain.usecase.student

import com.example.collegueapp.domain.model.student.ClassModel
import com.example.collegueapp.domain.repository.student.classes.ClassesRepository
import javax.inject.Inject

class GetAllClassesUseCase @Inject constructor(
    private val repository: ClassesRepository
) {
    suspend operator fun invoke(userId: String): Result<List<ClassModel>> =
        repository.getAllClasses(userId = userId)
}