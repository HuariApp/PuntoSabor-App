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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.data.model.Promo
import com.example.huariquehub_mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerPromosScreen(
    ownerId: Int,
    onBack: () -> Unit,
    onAddPromo: () -> Unit,
    onEditPromo: (Int) -> Unit = {},
    viewModel: OwnerPromosViewModel = viewModel()
) {
    LifecycleResumeEffect(ownerId) {
        viewModel.load(ownerId)
        onPauseOrDispose { }
    }

    val ownerPromos = viewModel.promos
    val activePromoCount = remember(ownerPromos) { ownerPromos.count { it.isActive } }
    var showDeleteDialog by remember { mutableStateOf<Promo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mis Promos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SurfaceColor)
                        Text("$activePromoCount activas", fontSize = 12.sp, color = SurfaceColor.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = SurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrownDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPromo, containerColor = OrangePrimary, contentColor = SurfaceColor) {
                Icon(Icons.Default.Add, "Nueva promo")
            }
        }
    ) { innerPadding ->
        if (ownerPromos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(WarmWhite),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Sin promos aún", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Crea tu primera promoción", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).background(WarmWhite),
                contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ownerPromos, key = { it.id }) { promo ->
                    PromoCard(
                        promo = promo,
                        onEdit = { onEditPromo(promo.id) },
                        onDelete = { showDeleteDialog = promo }
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { promo ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar promo") },
            text = { Text("¿Eliminar \"${promo.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(promo.id)
                    showDeleteDialog = null
                }) {
                    Text("Eliminar", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PromoCard(promo: Promo, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge tipo + descuento
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(OrangeLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (promo.discount > 0) {
                        Text("${promo.discount}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        Text("off", fontSize = 9.sp, color = BrownMedium, letterSpacing = 0.5.sp)
                    } else {
                        Text(promoEmoji(promo.type), fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(promo.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BrownDark)
                    Surface(shape = RoundedCornerShape(6.dp), color = if (promo.isActive) OrangeLight else BackgroundSoft) {
                        Text(
                            if (promo.isActive) "Activa" else "Inactiva",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = if (promo.isActive) OrangePrimary else TextTertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(promo.note, fontSize = 12.sp, color = TextSecondary)
                if (!promo.code.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Código: ${promo.code}", fontSize = 11.sp, color = BrownMedium, fontWeight = FontWeight.Medium)
                }
                if (promo.maxUses != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { promo.currentUses.toFloat() / promo.maxUses },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = OrangePrimary,
                        trackColor = OrangeLight
                    )
                    Text("${promo.currentUses}/${promo.maxUses} usos", fontSize = 10.sp, color = TextTertiary)
                }
            }

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

private fun promoEmoji(type: String) = when (type) {
    "2x1"        -> "2×1"
    "menu"       -> "🍽️"
    "happy-hour" -> "🍹"
    "descuento"  -> "%"
    else         -> "🎉"
}
