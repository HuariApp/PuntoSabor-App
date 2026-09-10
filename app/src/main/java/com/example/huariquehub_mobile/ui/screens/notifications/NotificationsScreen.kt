package com.example.huariquehub_mobile.ui.screens.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huariquehub_mobile.ui.theme.*

/**
 * Lista de notificaciones del usuario (US12). Al tocar una se marca como leída.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val unreadCount = viewModel.unreadCount

                    Text(
                        text = if (unreadCount > 0) {
                            "Mis notificaciones ($unreadCount)"
                        } else {
                            "Mis notificaciones"
                        },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 8.dp)
        ) {
            viewModel.error?.let { message ->
                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when {
                viewModel.isLoading && viewModel.notifications.isEmpty() -> {
                    CircularProgressIndicator(
                        color = OrangePrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                viewModel.notifications.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Sin notificaciones",
                            tint = OrangePrimary,
                            modifier = Modifier.size(44.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Aún no tienes notificaciones",
                            color = BrownDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Aquí aparecerán novedades sobre huariques, promociones y reseñas.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(viewModel.notifications, key = { it.id }) { n ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!n.isRead) {
                                            viewModel.markAsRead(n.id)
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (n.isRead) {
                                        SurfaceColor
                                    } else {
                                        OrangeLight
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notificación",
                                        tint = OrangePrimary
                                    )

                                    Spacer(Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = n.title,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrownDark,
                                            fontSize = 15.sp
                                        )

                                        Text(
                                            text = n.body,
                                            color = TextSecondary,
                                            fontSize = 13.sp
                                        )

                                        if (n.date.isNotBlank()) {
                                            Text(
                                                text = n.date,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (!n.isRead) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Surface(
                                                color = OrangePrimary,
                                                shape = CircleShape
                                            ) {
                                                Box(Modifier.size(10.dp))
                                            }

                                            Spacer(Modifier.height(4.dp))

                                            Text(
                                                text = "Nuevo",
                                                color = OrangePrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}