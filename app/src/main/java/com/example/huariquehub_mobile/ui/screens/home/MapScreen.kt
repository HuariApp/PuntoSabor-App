package com.example.huariquehub_mobile.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.data.model.Huarique
import com.example.huariquehub_mobile.ui.theme.OrangePrimary
import com.example.huariquehub_mobile.ui.theme.SurfaceColor
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val DEFAULT_MAP_LAT = -12.0464
private const val DEFAULT_MAP_LON = -77.0428
private const val DEFAULT_MAP_ZOOM = 12.0

/**
 * Mapa in-app con marcadores de huariques (US02).
 *
 * Usa osmdroid con tiles de OpenStreetMap (sin API key). Solo se muestran los
 * huariques con coordenadas válidas; al tocar un marcador se abre su ventana
 * de información con nombre, dirección y calificación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // Identificador requerido por osmdroid para descargar tiles de OSM.
        Configuration.getInstance().userAgentValue = context.packageName
        if (viewModel.huariques.isEmpty()) viewModel.load()
    }

    val located = remember(viewModel.huariques) {
        viewModel.huariques.filter { it.latitude != 0.0 || it.longitude != 0.0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Huariques en el mapa", color = SurfaceColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = SurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (viewModel.isLoading && viewModel.huariques.isEmpty()) {
                CircularProgressIndicator(
                    color = OrangePrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            val start = located.firstOrNull()
                            controller.setZoom(DEFAULT_MAP_ZOOM)
                            controller.setCenter(
                                if (start != null) GeoPoint(start.latitude, start.longitude)
                                else GeoPoint(DEFAULT_MAP_LAT, DEFAULT_MAP_LON)
                            )
                        }
                    },
                    update = { map ->
                        map.overlays.clear()
                        located.forEach { h -> map.overlays.add(buildMarker(map, h)) }
                        map.invalidate()
                    }
                )
                if (located.isEmpty() && !viewModel.isLoading) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = SurfaceColor,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            "No hay huariques con ubicación para mostrar.",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

private fun buildMarker(map: MapView, h: Huarique): Marker = Marker(map).apply {
    position = GeoPoint(h.latitude, h.longitude)
    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    title = h.name
    // Resumen con dirección y calificación (US02).
    snippet = buildString {
        append(h.district)
        if (h.address.isNotBlank()) append(" · ${h.address}")
        append("\n★ %.1f (${h.reviewCount})".format(h.rating))
    }
}
