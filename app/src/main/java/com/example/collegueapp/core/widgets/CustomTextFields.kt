package com.example.collegueapp.core.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.collegueapp.R
import com.example.collegueapp.ui.theme.AppTypography

@Composable
fun CustomTextField(
    value: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isVisible: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    onIconClick: () -> Unit = {},
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                style = AppTypography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { onIconClick() }) {
                    Icon(
                        painter = if (isVisible) painterResource(R.drawable.ic_hide) else painterResource(
                            R.drawable.ic_visibility
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                null
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = { if (isError) Text(errorMessage!!) else null },
        visualTransformation = if (isPassword && !isVisible) {
            PasswordVisualTransformation()
        } else VisualTransformation.None
    )

}