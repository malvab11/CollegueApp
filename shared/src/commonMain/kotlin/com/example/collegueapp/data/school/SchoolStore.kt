package com.example.collegueapp.data.school

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.collegueapp.platform.SchoolStorage
import kotlinx.serialization.json.Json

class SchoolStore(private val storage: SchoolStorage) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private var schoolEngine = SchoolEngine(runCatching {
        storage.get("school")?.let { json.decodeFromString<SchoolData>(it) }
    }.getOrNull() ?: SchoolSeed.create())
    var data by mutableStateOf(schoolEngine.data)
        private set
    var account by mutableStateOf(SchoolAccounts.all.find { it.id == storage.get("session") })
        private set
    fun engine(): SchoolEngine = schoolEngine
    fun currentAccount(): SchoolAccount? { data; return account?.let(schoolEngine::effectiveAccount) }
    fun mutate(action: SchoolEngine.() -> Unit) {
        schoolEngine.action()
        data = schoolEngine.data
        storage.put("school", json.encodeToString(data))
    }
    fun login(username: String, password: String) {
        val found = SchoolAccounts.find(username)
        require(found != null && password == SchoolAccounts.PASSWORD) { "Usuario o contraseña incorrectos." }
        storage.put("session", found.id)
        account = found
        mutate { dailyReminders() }
    }
    fun logout() {
        storage.put("session", null)
        account = null
    }
    fun requestRecovery(username: String) {
        require(username.isNotBlank()) { "Ingresa tu usuario institucional." }
        SchoolAccounts.find(username)?.let { owner -> mutate { ticket(owner, "Recuperación de acceso", "Solicitud de recuperación del acceso institucional de ${owner.name}.") } }
    }
    fun reset(actor: SchoolAccount) {
        require(actor.role == SchoolRole.ADMIN)
        schoolEngine = SchoolEngine()
        data = schoolEngine.data
        storage.put("school", json.encodeToString(data))
    }
}
