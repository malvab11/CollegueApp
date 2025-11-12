package com.example.collegueapp.data.remote.datasource.student.classes

import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.example.collegueapp.domain.datasources.student.classes.ClassesDataSource
import com.example.collegueapp.domain.model.login.UserModel
import com.example.collegueapp.domain.model.student.ClassModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClassesDataSourceFirebaseImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ClassesDataSource {
    override suspend fun getAllClasses(userId: String): List<ClassModel> {
//        val routesDocs =
//            firestore.collection("students").whereEqualTo("userId",userId).get()
//                .await()
//        val routes =
//            routesDocs.documents.map { it.toObject(RoutesModel::class.java) }
//        return routes
        return emptyList()
    }

}

