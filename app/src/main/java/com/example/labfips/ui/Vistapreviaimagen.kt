package com.example.labfips.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private sealed interface EstadoImagen {
    object Cargando : EstadoImagen
    object Error : EstadoImagen
    class Lista(val imagen: ImageBitmap) : EstadoImagen
}

/**
 * Muestra la imagen de un Uri (por ejemplo, la foto elegida de la galería).
 * Se carga en segundo plano y reducida, para no gastar memoria. No requiere librerías extra.
 * Usa el modifier para darle tamaño y esquinas (por ejemplo .height(200.dp).clip(...)).
 */
@Composable
fun VistaPreviaImagen(
    uri: Uri?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val estado by produceState<EstadoImagen>(initialValue = EstadoImagen.Cargando, key1 = uri) {
        value = if (uri == null) {
            EstadoImagen.Error
        } else {
            val bitmap = withContext(Dispatchers.IO) { cargarBitmap(context, uri) }
            if (bitmap != null) EstadoImagen.Lista(bitmap.asImageBitmap()) else EstadoImagen.Error
        }
    }

    when (val e = estado) {
        is EstadoImagen.Lista -> Image(
            bitmap = e.imagen,
            contentDescription = "Fotografía de evidencia",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )

        EstadoImagen.Cargando -> Box(
            modifier = modifier.background(Color(0xFFE8ECF3)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }

        EstadoImagen.Error -> Box(
            modifier = modifier.background(Color(0xFFE8ECF3)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ImageNotSupported, contentDescription = null, tint = Color(0xFF9CA3AF))
                Spacer(Modifier.height(4.dp))
                Text("No se pudo mostrar la imagen", color = Color(0xFF6B7385), fontSize = 12.sp)
            }
        }
    }
}

/** Lee la imagen reducida (máx. ~1024 px) y la gira según la orientación con la que se tomó la foto. */
private fun cargarBitmap(context: Context, uri: Uri, maxLado: Int = 1024): Bitmap? {
    return try {
        val resolver = context.contentResolver

        // 1) Solo leer las medidas
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }

        // 2) Calcular cuánto reducirla
        var muestra = 1
        val ladoMayor = maxOf(medidas.outWidth, medidas.outHeight)
        while (ladoMayor / muestra > maxLado) muestra *= 2

        // 3) Leerla reducida
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        } ?: return null

        // 4) Corregir la rotación (muchas fotos del celular vienen "de lado")
        val grados = resolver.openInputStream(uri)?.use { entrada ->
            when (ExifInterface(entrada).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } ?: 0

        if (grados == 0) {
            bitmap
        } else {
            val matriz = Matrix().apply { postRotate(grados.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        }
    } catch (e: Exception) {
        null
    }
}