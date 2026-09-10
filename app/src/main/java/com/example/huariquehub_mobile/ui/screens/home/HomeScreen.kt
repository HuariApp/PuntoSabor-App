package com.example.huariquehub_mobile.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.data.model.*
import com.example.huariquehub_mobile.ui.components.HuariqueImage
import com.example.huariquehub_mobile.ui.theme.*
import com.example.huariquehub_mobile.R

private val HomeBackground = Color(0xFFD4E8A0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onHuariqueClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onMapClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPreferencesClick: () -> Unit = {},
    userRole: UserRole = UserRole.CONSUMER,
    viewModel: HomeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }

    val filteredHuariques = viewModel.huariques.filter { huarique ->
        val matchesSearch = searchQuery.isEmpty() ||
                huarique.name.contains(searchQuery, ignoreCase = true) ||
                huarique.district.contains(searchQuery, ignoreCase = true) ||
                huarique.category.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == "Todas" || huarique.category == selectedCategory
        val matchesFavorites = !viewModel.favoritesOnly || huarique.id in viewModel.favoriteIds

        matchesSearch && matchesCategory && matchesFavorites
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_puntosabor),
                            contentDescription = "logo",
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "PuntoSabor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = BrownDark
                            )

                            Text(
                                text = "Lima, Perú",
                                fontSize = 12.sp,
                                color = BrownDark.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onMapClick) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = "Ver en mapa",
                            tint = BrownDark
                        )
                    }

                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = BrownDark
                        )
                    }

                    IconButton(onClick = onPreferencesClick) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Preferencias",
                            tint = BrownDark
                        )
                    }

                    IconButton(onClick = onProfileClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrownDark.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = BrownDark
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HomeBackground)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmWhite),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // banner hero
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    HomeBackground,
                                    HomeBackground.copy(alpha = 0.0f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            text = "Descubre los mejores",
                            fontSize = 16.sp,
                            color = BrownDark.copy(alpha = 0.9f)
                        )

                        Text(
                            text = "Huariques cerca de ti",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrownDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // barra de búsqueda
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Buscar huariques, distritos...",
                                    color = TextSecondary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = OrangePrimary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = null,
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = SurfaceColor,
                                unfocusedContainerColor = SurfaceColor
                            )
                        )
                    }
                }
            }

            // banner propietario
            if (userRole == UserRole.OWNER) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏪", fontSize = 28.sp)

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Eres propietario",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = SurfaceColor
                                )

                                Text(
                                    "Gestiona tus locales y promos",
                                    fontSize = 12.sp,
                                    color = SurfaceColor.copy(alpha = 0.75f)
                                )
                            }

                            TextButton(onClick = onProfileClick) {
                                Text(
                                    "Mi panel",
                                    color = OrangeDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // sugeridos para ti
            if (viewModel.suggestions.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "Sugeridos para ti ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BrownDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(viewModel.suggestions) { s ->
                                Surface(
                                    onClick = { onHuariqueClick(s.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceColor,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.width(180.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            s.name,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrownDark,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(Modifier.height(2.dp))

                                        Text(
                                            "${s.category} · ${s.district}",
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = StarYellow,
                                                modifier = Modifier.size(14.dp)
                                            )

                                            Spacer(Modifier.width(2.dp))

                                            Text(
                                                "%.1f".format(s.rating),
                                                fontSize = 12.sp,
                                                color = BrownDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // categorías
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Categorías",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BrownDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.categories) { category ->
                            CategoryChip(
                                category = category,
                                selected = selectedCategory == category.name,
                                onClick = { selectedCategory = category.name }
                            )
                        }
                    }
                }
            }

            // filtros rápidos
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = viewModel.nearbyOnly,
                        onClick = { viewModel.toggleNearby() },
                        label = { Text("Cerca de mí") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = SurfaceColor,
                            selectedLeadingIconColor = SurfaceColor
                        )
                    )

                    FilterChip(
                        selected = viewModel.favoritesOnly,
                        onClick = { viewModel.toggleFavoritesOnly() },
                        label = { Text("Favoritos") },
                        leadingIcon = {
                            Icon(
                                if (viewModel.favoritesOnly) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = SurfaceColor,
                            selectedLeadingIconColor = SurfaceColor
                        )
                    )
                }
            }

            // título resultados
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (
                            searchQuery.isEmpty() &&
                            selectedCategory == "Todas" &&
                            !viewModel.favoritesOnly &&
                            !viewModel.nearbyOnly
                        ) {
                            "Destacados 🔥"
                        } else {
                            "${filteredHuariques.size} resultados encontrados"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BrownDark
                    )

                    TextButton(
                        onClick = {
                            searchQuery = ""
                            selectedCategory = "Todas"
                            viewModel.clearFilters()
                        }
                    ) {
                        Text(
                            "Limpiar filtros",
                            color = OrangePrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // lista de huariques
            when {
                viewModel.isLoading && viewModel.huariques.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangePrimary)
                        }
                    }
                }

                viewModel.error != null && viewModel.huariques.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚠️", fontSize = 48.sp)

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    viewModel.error ?: "",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.load() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = OrangePrimary
                                    )
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }

                filteredHuariques.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 48.sp)

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "No encontramos huariques",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )

                                Text(
                                    text = "Prueba con otro distrito, categoría o limpia los filtros.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                TextButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedCategory = "Todas"
                                        viewModel.clearFilters()
                                    }
                                ) {
                                    Text(
                                        "Limpiar búsqueda",
                                        color = OrangePrimary
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    items(filteredHuariques) { huarique ->
                        HuariqueCard(
                            huarique = huarique,
                            isFavorite = huarique.id in viewModel.favoriteIds,
                            onToggleFavorite = { viewModel.toggleFavorite(huarique.id) },
                            onClick = { onHuariqueClick(huarique.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) OrangePrimary else SurfaceColor,
        shadowElevation = if (selected) 4.dp else 1.dp,
        border = if (!selected) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(category.icon, fontSize = 16.sp)

            Text(
                text = category.name,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) SurfaceColor else TextPrimary
            )
        }
    }
}

@Composable
fun HuariqueCard(
    huarique: Huarique,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val status = huarique.openStatus()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                HuariqueImage(
                    name = huarique.name,
                    url = huarique.imageUrl,
                    modifier = Modifier.fillMaxSize(),
                    emojiSize = 56.sp
                )

                // badge categoría
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = OrangePrimary
                ) {
                    Text(
                        huarique.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = SurfaceColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // botón favorito
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) {
                            "Quitar de favoritos"
                        } else {
                            "Agregar a favoritos"
                        },
                        tint = if (isFavorite) ErrorRed else SurfaceColor
                    )
                }

                // estado abierto/cerrado
                if (status != OpenStatus.UNKNOWN) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (status == OpenStatus.OPEN) {
                            YellowGreen
                        } else {
                            BrownDark.copy(alpha = 0.85f)
                        }
                    ) {
                        Text(
                            status.label,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = if (status == OpenStatus.OPEN) BrownDark else SurfaceColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = huarique.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = BrownDark,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = "%.1f".format(huarique.rating),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = BrownDark
                        )

                        Text(
                            " (${huarique.reviewCount})",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        "${huarique.district} · ${huarique.address}",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        huarique.hours,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(
                        "Ver detalles",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}