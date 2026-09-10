@file:Suppress("UNUSED_VALUE")
package com.example.huariquehub_mobile.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.ui.theme.*

// ── Shared helpers (mirrored from CreateEditHuariqueScreen — file-private scope) ──

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextTertiary,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
        content()
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = OrangePrimary,
    unfocusedBorderColor = DividerWarm,
    focusedLabelColor = OrangePrimary,
    cursorColor = OrangePrimary,
    focusedContainerColor = SurfaceColor,
    unfocusedContainerColor = SurfaceColor
)

// ────────────────────────────────────────────────────────────────────────────────

private data class PromoType(val key: String, val label: String, val emoji: String)

private val promoTypes = listOf(
    PromoType("2x1",        "2×1",          "🍗"),
    PromoType("descuento",  "Descuento %",  "%"),
    PromoType("menu",       "Menú especial","🍽️"),
    PromoType("happy-hour", "Happy hour",   "🍹"),
    PromoType("otro",       "Otro",         "🎉")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditPromoScreen(
    promoId: Int? = null,
    ownerId: Int = 0,
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: CreateEditPromoViewModel = viewModel()
) {
    val isEditing = promoId != null
    LaunchedEffect(promoId, ownerId) { viewModel.init(promoId, ownerId) }
    val existing = viewModel.existing

    var title    by remember { mutableStateOf("") }
    var note     by remember { mutableStateOf("") }
    var typeKey  by remember { mutableStateOf("otro") }
    var discount by remember { mutableStateOf("") }
    var code     by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate  by remember { mutableStateOf("") }
    var maxUses  by remember { mutableStateOf("") }

    var selectedHuariqueId by remember { mutableStateOf<Int?>(null) }

    var expandedType by remember { mutableStateOf(false) }
    var expandedHuarique by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf("") }
    val isSaving = viewModel.isSaving
    val errorMessage = validationError.ifBlank { viewModel.error.orEmpty() }

    // Prefill al cargar la promo existente (modo edición).
    LaunchedEffect(existing) {
        existing?.let {
            title = it.title
            note = it.note
            typeKey = it.type
            discount = if (it.discount > 0) it.discount.toString() else ""
            code = it.code ?: ""
            startDate = it.startDate ?: ""
            endDate = it.endDate ?: ""
            maxUses = it.maxUses?.toString() ?: ""
            selectedHuariqueId = it.huariqueId
        }
    }

    val ownerHuariques = viewModel.ownerHuariques
    val selectedType = promoTypes.first { it.key == typeKey }
    val showDiscount = typeKey == "descuento" || typeKey == "2x1"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar promo" else "Nueva promo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SurfaceColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = SurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrownDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmWhite)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Información básica ──────────────────────────────────────────
            SectionLabel("Información básica")

            FormField(label = "Título de la promo *") {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; validationError = "" },
                    placeholder = { Text("Ej. 2×1 Pollo Hoy") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
            }

            FormField(label = "Tipo de promo *") {
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedType.emoji}  ${selectedType.label}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        promoTypes.forEach { pt ->
                            DropdownMenuItem(
                                text = { Text("${pt.emoji}  ${pt.label}") },
                                onClick = {
                                    typeKey = pt.key
                                    if (!showDiscount) discount = ""
                                    expandedType = false
                                }
                            )
                        }
                    }
                }
            }

            FormField(label = "Descripción / nota *") {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it; validationError = "" },
                    placeholder = { Text("Ej. Solo Lun–Vie de 12:00 a 16:00") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
            }

            // ── Descuento ───────────────────────────────────────────────────
            if (showDiscount) {
                SectionLabel("Descuento")

                FormField(label = "Porcentaje de descuento") {
                    OutlinedTextField(
                        value = discount,
                        onValueChange = { discount = it },
                        placeholder = { Text("Ej. 20") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Text("%", fontSize = 14.sp, color = TextSecondary,
                                modifier = Modifier.padding(end = 12.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )
                }
            }

            // ── Código y límite de uso ──────────────────────────────────────
            SectionLabel("Código y límites")

            FormField(label = "Código de canje (opcional)") {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = { Text("Ej. PROMO20") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
            }

            FormField(label = "Máximo de usos (opcional)") {
                OutlinedTextField(
                    value = maxUses,
                    onValueChange = { maxUses = it },
                    placeholder = { Text("Ej. 100 — deja vacío para ilimitado") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors()
                )
            }

            // ── Vigencia ────────────────────────────────────────────────────
            SectionLabel("Vigencia")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormField(label = "Fecha inicio", modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        placeholder = { Text("AAAA-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )
                }
                FormField(label = "Fecha fin", modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        placeholder = { Text("AAAA-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )
                }
            }

            // ── Local asociado ──────────────────────────────────────────────
            SectionLabel("Local asociado")

            FormField(label = "Huarique (opcional)") {
                ExposedDropdownMenuBox(
                    expanded = expandedHuarique,
                    onExpandedChange = { expandedHuarique = it }
                ) {
                    OutlinedTextField(
                        value = ownerHuariques.find { it.id == selectedHuariqueId }?.name
                            ?: "Sin asignar",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHuarique)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedHuarique,
                        onDismissRequest = { expandedHuarique = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin asignar") },
                            onClick = { selectedHuariqueId = null; expandedHuarique = false }
                        )
                        ownerHuariques.forEach { h ->
                            DropdownMenuItem(
                                text = { Text(h.name) },
                                onClick = { selectedHuariqueId = h.id; expandedHuarique = false }
                            )
                        }
                    }
                }
            }

            // ── Error ───────────────────────────────────────────────────────
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = ErrorRed, fontSize = 13.sp)
            }

            // ── Guardar ─────────────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        title.isBlank() -> validationError = "El título es obligatorio."
                        note.isBlank()  -> validationError = "La descripción es obligatoria."
                        else -> {
                            validationError = ""
                            viewModel.save(
                                id = promoId,
                                title = title,
                                note = note,
                                typeKey = typeKey,
                                discountText = discount,
                                huariqueId = selectedHuariqueId
                            ) { onSave() }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = SurfaceColor,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        if (isEditing) "Guardar cambios" else "Publicar promo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
