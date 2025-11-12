package com.example.collegueapp.presentation.common.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegueapp.R
import com.example.collegueapp.core.widgets.CustomButtons
import com.example.collegueapp.core.widgets.CustomTextField
import com.example.collegueapp.ui.theme.AppTypography

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = hiltViewModel(), onLoginSuccess: () -> Unit) {

    val context = LocalContext.current

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }

    val uiState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        loginViewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState is LoginState.Loading) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.auth_loading),
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
                )
            } else {
                MainLogo()
                TitlesSection()
                InputSection(
                    emailState = emailState,
                    passwordState = passwordState,
                    isPasswordVisible = isPasswordVisible,
                    state = uiState
                )
                ButtonSection(onButtonClicked = {
                    loginViewModel.login(
                        username = emailState.value,
                        password = passwordState.value
                    )
                })
            }
        }
    }
}

@Composable
private fun ButtonSection(onButtonClicked: () -> Unit) {
    CustomButtons(
        text = stringResource(R.string.auth_sign_in_button),
        onClick = {
            onButtonClicked()
        }
    )

    Text(
        text = stringResource(R.string.auth_forgot_password),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable { }
    )
}

@Composable
private fun InputsLabel(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    isVisible: Boolean = true,
    onIconClick: () -> Unit = {},
    onValueChange: (String) -> Unit,
    errorMessage: String?
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = AppTypography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Start
        )

        CustomTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            isPassword = isPassword,
            keyboardType = keyboardType,
            isVisible = isVisible,
            isError = errorMessage != null,
            errorMessage = errorMessage,
            onIconClick = { onIconClick() }
        )
    }
}

@Composable
private fun InputSection(
    emailState: MutableState<String>,
    passwordState: MutableState<String>,
    isPasswordVisible: MutableState<Boolean>,
    state: LoginState,
) {

    val errorMessage = if (state is LoginState.Error) "Credenciales Incorrectas" else null

    Column {
        InputsLabel(
            label = stringResource(R.string.auth_input_1_label),
            value = emailState.value,
            placeholder = stringResource(R.string.auth_input_1_placeholder),
            leadingIcon = Icons.Default.Person,
            isPassword = false,
            errorMessage = errorMessage,
            onValueChange = { emailState.value = it },
            keyboardType = KeyboardType.Email,
        )

        InputsLabel(
            label = stringResource(R.string.auth_input_2_label),
            value = passwordState.value,
            placeholder = stringResource(R.string.auth_input_2_placeholder),
            leadingIcon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            errorMessage = errorMessage,
            isVisible = isPasswordVisible.value,
            onIconClick = { isPasswordVisible.value = !isPasswordVisible.value },
            onValueChange = { passwordState.value = it }
        )

    }
}

@Composable
private fun TitlesSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Headline
        Text(
            text = stringResource(R.string.auth_title),
            style = AppTypography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
        )

        // Body Text
        Text(
            text = stringResource(R.string.auth_sub_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun MainLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.extraLarge
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.clip(CircleShape)
        )
    }
}

