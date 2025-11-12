package com.example.collegueapp.data.remote.datasource.login

import com.example.collegueapp.domain.datasources.login.AuthDataSource
import com.example.collegueapp.domain.model.login.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSourceFirebaseImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthDataSource {
    override suspend fun authenticateUser(
        username: String,
        password: String
    ): UserModel {
        // Primero autenticar al usuario
        val authResult = auth.signInWithEmailAndPassword(username, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Autenticación Fallida")

        // Luego obtener su perfil
        val snapshot = firestore.collection("users").document(uid).get().await()

        val name = snapshot.getString("name") ?: ""
        val role = snapshot.getString("role") ?: ""

        return UserModel(
            userId = uid,
            name = name,
            role = role,
        )
    }
}