package com.example.huariquehub_mobile.ui.screens.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.ui.theme.*

/**
 * Preferencias del usuario (US17) y configuración de notificaciones (US11).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    onBack: () -> Unit,
    viewModel: PreferencesViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis preferencias",
                        color = SurfaceColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = SurfaceColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangePrimary
                )
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
            Text(
                text = "Personaliza tus recomendaciones (US17) y decide si quieres recibir notificaciones (US11).",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.preferredCategory,
                onValueChange = viewModel::onCategoryChange,
                label = { Text("Tipo de comida preferida") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.maxBudget,
                onValueChange = viewModel::onBudgetChange,
                label = { Text("Presupuesto máximo (S/)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.preferredDistrict,
                onValueChange = viewModel::onDistrictChange,
                label = { Text("Distrito preferido") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Notificaciones",
                        fontWeight = FontWeight.SemiBold,
                        color = BrownDark
                    )

                    Text(
                        text = "Recibir avisos de la app",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = viewModel.notificationsEnabled,
                    onCheckedChange = viewModel::onNotificationsChange,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = OrangePrimary
                    )
                )
            }

            viewModel.error?.let {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = it,
                    color = ErrorRed,
                    fontSize = 13.sp
                )
            }

            if (viewModel.saved) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "✓ Preferencias guardadas",
                    color = BrownDark,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.save() },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary
                )
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = SurfaceColor,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Guardar preferencias",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { viewModel.clearInputs() },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Limpiar campos")
            }
        }
    }
}