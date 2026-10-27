package com.example.collegueapp.presentation.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.collegueapp.data.school.SchoolStore
import com.example.collegueapp.ui.theme.SchoolNavy
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@Composable
fun LoginScreen(store: SchoolStore) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var visible by rememberSaveable { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var recovery by remember { mutableStateOf(false) }
    var recoverySent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    fun signIn() {
        if (loading || username.isBlank() || password.isBlank()) return
        keyboard?.hide()
        loading = true
        scope.launch {
            yield()
            runCatching { store.login(username, password) }.onFailure { error = it.message ?: "No pudimos iniciar sesión. Inténtalo de nuevo." }
            loading = false
        }
    }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(SchoolNavy, Color(0xFF24466B), MaterialTheme.colorScheme.background), endY = 1500f))) {
        Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Spacer(Modifier.height(12.dp))
            SchoolLogo(Modifier.size(112.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Collegue", style = MaterialTheme.typography.displayLarge, color = Color.White)
                Text(SchoolName, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                Text(SchoolLocation, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
            }
            Card(Modifier.widthIn(max = 440.dp).fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Bienvenido a tu colegio", style = MaterialTheme.typography.headlineMedium)
                    Text("Todo lo que necesitas para acompañar cada día escolar.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(username, { username = it; error = null }, label = { Text("Usuario") }, leadingIcon = { Icon(Icons.Outlined.Person, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !loading, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, autoCorrectEnabled = false))
                    OutlinedTextField(password, { password = it; error = null }, label = { Text("Contraseña") }, leadingIcon = { Icon(Icons.Outlined.Lock, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !loading, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { signIn() }), visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { visible = !visible }) { Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, if (visible) "Ocultar contraseña" else "Mostrar contraseña") } })
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Button(onClick = ::signIn, enabled = !loading && username.isNotBlank() && password.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                        if (loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp) else Text("Ingresar", fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(onClick = { recovery = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("¿Olvidaste tu contraseña?") }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.VerifiedUser, null, Modifier.size(16.dp), MaterialTheme.colorScheme.onBackground)
                Text("Un espacio para tu comunidad educativa", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    if (recovery) SchoolFormSheet(SchoolForm("Recuperar acceso", listOf(FormField("username", "Usuario institucional", username)), body = "Ingresa tu usuario. Secretaría te ayudará a recuperar el acceso por los canales registrados en el colegio.", confirm = "Solicitar ayuda", submit = { values -> store.requestRecovery(values.getValue("username")); recoverySent = true }), onDismiss = { recovery = false })
    if (recoverySent) AlertDialog(onDismissRequest = { recoverySent = false }, title = { Text("Solicitud registrada") }, text = { Text("Si el usuario corresponde a una cuenta del colegio, Secretaría revisará la solicitud. También puedes comunicarte con la oficina de atención.") }, confirmButton = { TextButton(onClick = { recoverySent = false }) { Text("Entendido") } })
}
