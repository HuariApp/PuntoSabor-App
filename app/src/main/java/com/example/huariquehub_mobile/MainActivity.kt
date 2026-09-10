package com.example.huariquehub_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.huariquehub_mobile.ui.navigation.AppNavigation
import com.example.huariquehub_mobile.ui.theme.HuariqueHubMobileTheme

/** Punto de entrada de la app; monta el tema y el grafo de navegación. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuariqueHubMobileTheme {
                AppNavigation()
            }
        }
    }
}