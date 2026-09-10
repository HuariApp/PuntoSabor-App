package com.example.huariquehub_mobile.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.data.model.*
import com.example.huariquehub_mobile.ui.components.HuariqueImage
import com.example.huariquehub_mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuariqueDetailScreen(
    huariqueId: Int,
    onBack: () -> Unit,
    viewModel: HuariqueDetailViewModel = viewModel()
) {
    LaunchedEffect(huariqueId) { viewModel.load(huariqueId) }

    val context = LocalContext.current
    val huarique = viewModel.huarique
    val reviews = viewModel.reviews
    var showReviewDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Muestra mensajes de moderación (US08) y confirmación de reporte (US21).
    LaunchedEffect(viewModel.feedback) {
        viewModel.feedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeFeedback()
        }
    }

    if (huarique == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cargando...", color = SurfaceColor) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = SurfaceColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding).background(WarmWhite),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.error != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠️", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(viewModel.error ?: "", color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.load(huariqueId) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) { Text("Reintentar") }
                    }
                } else {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(huarique.name, fontWeight = FontWeight.Bold, color = SurfaceColor, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = SurfaceColor)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(huariqueId) }) {
                        Icon(
                            if (viewModel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (viewModel.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (viewModel.isFavorite) ErrorRed else SurfaceColor
                        )
                    }
                    IconButton(onClick = { shareHuarique(context, huarique) }) {
                        Icon(Icons.Default.Share, null, tint = SurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showReviewDialog = true },
                containerColor = OrangePrimary,
                contentColor = SurfaceColor,
                icon = { Icon(Icons.Default.Edit, null) },
                text = { Text("Escribir reseña") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmWhite),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Imagen hero (con fallback a emoji)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    HuariqueImage(
                        url = huarique.imageUrl,
                        modifier = Modifier.fillMaxSize(),
                        emojiSize = 80.sp
                    )
                    // Badge rating
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = BrownDark.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, null, tint = StarYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("%.1f".format(huarique.rating), color = SurfaceColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(" (${huarique.reviewCount})", color = SurfaceColor.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Información principal
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Nombre y categoría
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = huarique.name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrownDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = OrangeLight
                                ) {
                                    Text(
                                        huarique.category,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 12.sp,
                                        color = BrownMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info de contacto
                        InfoRow(Icons.Default.LocationOn, "${huarique.address}, ${huarique.district}")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(Icons.Default.Phone, huarique.phone)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(Icons.Default.Schedule, huarique.hours)

                        // Estado Abierto/Cerrado (US20/US22)
                        val status = huarique.openStatus()
                        if (status != OpenStatus.UNKNOWN) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (status == OpenStatus.OPEN) YellowGreen else BrownDark.copy(alpha = 0.85f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Schedule, null,
                                        tint = if (status == OpenStatus.OPEN) BrownDark else SurfaceColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        status.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (status == OpenStatus.OPEN) BrownDark else SurfaceColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Descripción
                        Text("Acerca de", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrownDark)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(huarique.description, fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones de acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { dialPhone(context, huarique.phone) },
                                enabled = huarique.phone.isNotBlank(),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary)
                            ) {
                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Llamar", fontSize = 13.sp)
                            }
                            OutlinedButton(
                                onClick = { openWhatsApp(context, huarique.phone, huarique.name) },
                                enabled = huarique.phone.isNotBlank(),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 13.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { openMap(context, huarique) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(Icons.Default.Map, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver en el mapa", fontSize = 13.sp)
                        }

                        // Reportar información incorrecta (US21)
                        TextButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Flag, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reportar información", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Promociones del huarique (US26 lado cliente)
            if (viewModel.promos.isNotEmpty()) {
                item {
                    Text(
                        "Promociones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = BrownDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                items(viewModel.promos, key = { it.id }) { promo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(OrangeLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (promo.discount > 0) "-${promo.discount}%" else "🎁",
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(promo.title, fontWeight = FontWeight.SemiBold, color = BrownDark, fontSize = 15.sp)
                                if (promo.note.isNotBlank())
                                    Text(promo.note, color = TextSecondary, fontSize = 12.sp)
                            }
                            TextButton(onClick = { viewModel.usePromo(promo) }) {
                                Text("Canjear", color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Reseñas header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Reseñas (${reviews.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = BrownDark
                    )
                    // Rating promedio
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star, null,
                                tint = if (index < huarique.rating.toInt()) StarYellow else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (reviews.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💬", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aún no hay reseñas", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("¡Sé el primero en opinar!", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(reviews, key = { it.id }) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }

    // Dialog para nueva reseña
    if (showReviewDialog) {
        NewReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(huariqueId, rating.toInt(), comment) {
                    showReviewDialog = false
                }
            }
        )
    }

    // Dialog para reportar información incorrecta (US21)
    if (showReportDialog) {
        ReportDialog(
            submitting = viewModel.reportSubmitting,
            onDismiss = { showReportDialog = false },
            onSubmit = { reason ->
                viewModel.reportHuarique(huariqueId, reason) { showReportDialog = false }
            }
        )
    }
}

// ── Acciones del detalle (llamar, WhatsApp, mapa, compartir) ────────────────
private fun dialPhone(context: Context, phone: String) {
    if (phone.isBlank()) return
    runCatching {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    }
}

private fun openWhatsApp(context: Context, phone: String, name: String) {
    val digits = phone.filter { it.isDigit() }
    if (digits.isEmpty()) return
    val text = Uri.encode("Hola, te encontré en PuntoSabor ($name).")
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$digits?text=$text"))
        )
    }
}

private fun openMap(context: Context, h: com.example.huariquehub_mobile.data.model.Huarique) {
    val uri = if (h.latitude != 0.0 || h.longitude != 0.0) {
        Uri.parse("geo:${h.latitude},${h.longitude}?q=${h.latitude},${h.longitude}(${Uri.encode(h.name)})")
    } else {
        Uri.parse("geo:0,0?q=${Uri.encode("${h.address}, ${h.district}")}")
    }
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
}

private fun shareHuarique(context: Context, h: com.example.huariquehub_mobile.data.model.Huarique?) {
    if (h == null) return
    val text = "¡Mira este huarique en PuntoSabor! ${h.name} (${h.category} · ${h.district})"
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    runCatching { context.startActivity(Intent.createChooser(send, "Compartir")) }
}

@Composable
private fun ReportDialog(
    submitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar información", fontWeight = FontWeight.Bold, color = BrownDark) },
        text = {
            Column {
                Text("¿Qué dato está incorrecto?", fontSize = 14.sp, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Describe el problema") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        focusedLabelColor = OrangePrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (reason.isNotBlank()) onSubmit(reason) },
                enabled = !submitting,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Enviar reporte") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        },
        containerColor = SurfaceColor
    )
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, color = TextSecondary)
    }
}

@Composable
fun ReviewCard(review: com.example.huariquehub_mobile.data.model.Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(OrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            review.userName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(review.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BrownDark)
                        Text(review.date, fontSize = 11.sp, color = TextSecondary)
                    }
                }
                // Estrellas
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, null,
                            tint = if (index < review.rating.toInt()) StarYellow else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(review.comment, fontSize = 14.sp, color = TextPrimary, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun NewReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    var rating by remember { mutableStateOf(5f) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva reseña", fontWeight = FontWeight.Bold, color = BrownDark) },
        text = {
            Column {
                Text("Tu calificación:", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = (index + 1).toFloat() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Star, null,
                                tint = if (index < rating.toInt()) StarYellow else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comparte tu experiencia") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        focusedLabelColor = OrangePrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (comment.isNotBlank()) onSubmit(rating, comment) },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Publicar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        },
        containerColor = SurfaceColor
    )
}
