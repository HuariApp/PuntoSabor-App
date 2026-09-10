package com.example.huariquehub_mobile.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.ui.theme.*

private const val MIN_PASSWORD_LENGTH = 6

/**
 * Recuperación de contraseña (US16). El usuario ingresa su correo, recibe un
 * mensaje de confirmación y puede establecer una nueva contraseña.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = solicitar, 2 = restablecer

    LaunchedEffect(email, newPassword) { viewModel.clearError() }

    val canSubmit = remember(email, newPassword, step, viewModel.isLoading) {
        !viewModel.isLoading &&
            email.isNotBlank() &&
            (step == 1 || newPassword.length >= MIN_PASSWORD_LENGTH)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña", color = SurfaceColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = SurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔑", fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                if (step == 1) "Ingresa tu correo para recuperar el acceso."
                else "Define una nueva contraseña para tu cuenta.",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (step == 2) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            viewModel.message?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = BrownDark, fontSize = 13.sp)
            }
            viewModel.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = ErrorRed, fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val normalizedEmail = email.trim()
                    if (step == 1) {
                        viewModel.forgotPassword(normalizedEmail) { step = 2 }
                    } else {
                        viewModel.resetPassword(normalizedEmail, newPassword) { }
                    }
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = SurfaceColor, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        if (step == 1) "Enviar instrucciones" else "Restablecer contraseña",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (step == 2) {
                TextButton(onClick = onBack) {
                    Text("Volver a iniciar sesión", color = OrangePrimary)
                }
            }
        }
    }
}
