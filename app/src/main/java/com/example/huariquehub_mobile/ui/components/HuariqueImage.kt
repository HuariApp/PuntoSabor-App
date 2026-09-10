package com.example.huariquehub_mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.huariquehub_mobile.R
import com.example.huariquehub_mobile.ui.theme.OrangeLight
import com.example.huariquehub_mobile.ui.theme.YellowGreen

// mapeo nombre del huarique -> drawable local
private val huariqueImageMap: Map<String, Int> = mapOf(
    "El Brasero"               to R.drawable.img_elbrasero,
    "Rincón Marino"            to R.drawable.img_rinconmarino,
    "Doña Peta Criolla"        to R.drawable.img_dona,
    "Chifa San Joy Lao"        to R.drawable.img_sanjoylao,
    "La Dulcería"              to R.drawable.img_dulcesazon,
    "La Esquinita del Menú"    to R.drawable.img_aesqui,
    "Café Aroma & Sabor"       to R.drawable.img_aromaysabor,
    "Pollos Don Tito"          to R.drawable.img_dontito,
    "Mar & Tierra"             to R.drawable.img_marytierra,
    "Café Central"             to R.drawable.img_cafecentral,
    "Parrilladas Don Mario"    to R.drawable.img_mario,
    "Brasa y Carbón"           to R.drawable.img_brasaycarbon,
    "Fuego Criollo"            to R.drawable.img_fuegocriollo,
    "La Parrilla del Norte"    to R.drawable.img_parrillanorte,
    "Punto Grill"              to R.drawable.img_puntogrill,
    "La Picantería Peruana"    to R.drawable.img_aesqui,
    "La Casa del Postre"       to R.drawable.img_lacasadelpostre,
    "Chifa Ping Chung Long"    to R.drawable.img_chifaping,
    "El Sabor Norteño"         to R.drawable.img_sabornorteno,
    "La Ola Marina"            to R.drawable.img_olamarina,
    "Menu Don Lucho"           to R.drawable.img_donlucho
)

/**
 * Imagen de un huarique. Prioriza la `imageUrl` del backend (la que el dueño
 * define para su local); si no hay, cae al mapa local de drawables como respaldo
 * de demo y, si tampoco, muestra un placeholder. Así la imagen que sube el dueño
 * manda, y seguimos teniendo una experiencia consistente sin conexión.
 */
@Composable
fun HuariqueImage(
    name: String? = null,
    url: String? = null,
    modifier: Modifier = Modifier,
    emojiSize: TextUnit = 56.sp
) {
    val localRes = name?.let { huariqueImageMap[it] }

    when {
        !url.isNullOrBlank() -> {
            coil.compose.SubcomposeAsyncImage(
                model = url,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = modifier,
                // Si la descarga falla, caemos al drawable local (si existe) o al placeholder.
                loading = { ImagePlaceholder(Modifier.fillMaxSize(), emojiSize) },
                error = {
                    if (localRes != null) {
                        Image(
                            painter = painterResource(id = localRes),
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ImagePlaceholder(Modifier.fillMaxSize(), emojiSize)
                    }
                }
            )
        }
        localRes != null -> {
            Image(
                painter = painterResource(id = localRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        else -> ImagePlaceholder(modifier, emojiSize)
    }
}

@Composable
private fun ImagePlaceholder(modifier: Modifier, emojiSize: TextUnit) {
    Box(
        modifier = modifier.background(
            Brush.horizontalGradient(listOf(OrangeLight, YellowGreen.copy(alpha = 0.5f)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Text("🍽️", fontSize = emojiSize)
    }
}
