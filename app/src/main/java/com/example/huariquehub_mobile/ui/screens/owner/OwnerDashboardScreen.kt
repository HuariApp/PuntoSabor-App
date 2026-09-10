@file:Suppress("UNUSED_VALUE")
package com.example.huariquehub_mobile.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.ui.theme.*
import androidx.compose.ui.draw.clip
import com.example.huariquehub_mobile.ui.components.HuariqueImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    ownerId: Int,
    onProfile: () -> Unit,
    onAddHuarique: () -> Unit,
    onEditHuarique: (Int) -> Unit,
    onManagePromos: () -> Unit = {},
    onSubscription: () -> Unit = {},
    viewModel: OwnerDashboardViewModel = viewModel()
) {
    // Recarga los locales del propietario al entrar y al volver de crear/editar.
    LifecycleResumeEffect(ownerId) {
        viewModel.load(ownerId)
        onPauseOrDispose { }
    }

    val ownerHuariques = viewModel.huariques
    val averageRating = remember(ownerHuariques) {
        if (ownerHuariques.isEmpty()) null
        else ownerHuariques.map { it.rating }.average()
    }
    var showDeleteDialog by remember { mutableStateOf<Huarique?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mi Panel", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SurfaceColor)
                        Text("Gestiona tus huariques", fontSize = 12.sp, color = SurfaceColor.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Default.Person, "Mi perfil", tint = SurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrownDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHuarique,
                containerColor = OrangePrimary,
                contentColor = SurfaceColor
            ) {
                Icon(Icons.Default.Add, "Agregar huarique")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmWhite),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Resumen estadísticas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(BrownDark, BrownDark.copy(alpha = 0.85f))))
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = ownerHuariques.size.toString(),
                            label = "Huariques"
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = averageRating?.let { "%.1f".format(it) } ?: "—",
                            label = "Rating prom."
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            value = ownerHuariques.sumOf { it.reviewCount }.toString(),
                            label = "Reseñas"
                        )
                    }
                }
            }

            // Acciones rápidas
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onManagePromos,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary)
                    ) {
                        Text("🎉 Mis promos", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    OutlinedButton(
                        onClick = onAddHuarique,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownMedium),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerWarm)
                    ) {
                        Text("+ Nuevo local", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Fila 2: suscripción
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                ) {
                    OutlinedButton(
                        onClick = onSubscription,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrownDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrownMedium.copy(alpha = 0.4f))
                    ) {
                        Text("⭐ Mi suscripción", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Título
            item {
                Text(
                    text = "Mis locales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrownDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Estado vacío
            if (ownerHuariques.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏪", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Aún no tienes locales", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Toca el botón + para agregar tu primer huarique",
                            color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                items(ownerHuariques, key = { it.id }) { huarique ->
                    OwnerHuariqueCard(
                        huarique = huarique,
                        onEdit = { onEditHuarique(huarique.id) },
                        onDelete = { showDeleteDialog = huarique }
                    )
                }
            }
        }
    }

    // Diálogo confirmación de borrado
    showDeleteDialog?.let { huarique ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar huarique") },
            text = { Text("¿Seguro que quieres eliminar \"${huarique.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(huarique.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, value: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SurfaceColor)
            Text(label, fontSize = 11.sp, color = SurfaceColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun OwnerHuariqueCard(
    huarique: Huarique,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto del huarique
            HuariqueImage(
                name = huarique.name,
                url = huarique.imageUrl,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
                emojiSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(huarique.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = BrownDark)
                Text(
                    "${huarique.category} · ${huarique.district}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = StarYellow, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("%.1f".format(huarique.rating), fontSize = 12.sp, color = BrownDark, fontWeight = FontWeight.Medium)
                    }
                    if (huarique.price > 0) {
                        Text("S/ %.0f".format(huarique.price), fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Acciones
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, "Editar", tint = BrownMedium, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = ErrorRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
