package com.example.huariquehub_mobile.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.ui.theme.*

/**
 * Perfil del usuario (CRUD de cuenta): ver datos, editar el nombre, cerrar
 * sesión y eliminar la cuenta. Punto de acceso a la suscripción y, para dueños,
 * al panel del propietario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    onGoToSubscription: () -> Unit = {},
    onGoToOwnerDashboard: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.load() }

    // Cuando la cuenta se elimina o se cierra sesión, salimos al login.
    LaunchedEffect(viewModel.deleted) {
        if (viewModel.deleted) onLoggedOut()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = SurfaceColor) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar + rol
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        viewModel.name.ifBlank { "Usuario" },
                        fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrownDark
                    )
                    Text(
                        if (viewModel.isOwner) "Dueño de huarique" else "Explorador",
                        color = TextSecondary, fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = DividerWarm)
            Spacer(Modifier.height(20.dp))

            // Email (solo lectura)
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = {},
                label = { Text("Correo") },
                enabled = false,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Nombre (editable)
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            viewModel.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = ErrorRed, fontSize = 13.sp)
            }
            if (viewModel.saved) {
                Spacer(Modifier.height(8.dp))
                Text("✓ Nombre actualizado", color = BrownDark, fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.save() },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = SurfaceColor, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar cambios", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Accesos rápidos
            if (viewModel.isOwner) {
                OutlinedButton(
                    onClick = onGoToOwnerDashboard,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Panel de dueño")
                }
                Spacer(Modifier.height(10.dp))
            }
            OutlinedButton(
                onClick = onGoToSubscription,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.CardMembership, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Mi suscripción")
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = DividerWarm)
            Spacer(Modifier.height(16.dp))

            // Cerrar sesión
            OutlinedButton(
                onClick = { viewModel.logout() },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
            Spacer(Modifier.height(10.dp))

            // Eliminar cuenta (acción destructiva con confirmación)
            TextButton(
                onClick = { showDeleteDialog = true },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = ErrorRed)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar mi cuenta", color = ErrorRed)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("Esta acción es permanente y no se puede deshacer. ¿Deseas eliminar tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAccount()
                }) { Text("Eliminar", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar", color = BrownDark) }
            },
            containerColor = SurfaceColor
        )
    }
}
