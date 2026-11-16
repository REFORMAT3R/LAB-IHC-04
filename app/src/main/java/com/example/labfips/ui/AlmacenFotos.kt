package com.example.labfips.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Copia la foto elegida de la galería al almacenamiento PRIVADO de la app.
 *
 * Por qué hace falta: la dirección (Uri) que entrega la galería es temporal y deja de
 * funcionar más adelante. La copia, en cambio, se queda guardada en la app.
 * La foto se reduce (máx. 1600 px) y se guarda como JPEG para que no pese demasiado.
 *
 * Es una función pesada: llámala en segundo plano (Dispatchers.IO).
 *
 * @return la dirección de la copia como texto (para guardarla en el reporte), o null si falló.
 */
fun guardarFotoReporte(context: Context, origen: Uri, idReporte: String): String? {
    return try {
        val bitmap = cargarBitmap(context, origen, maxLado = 1600) ?: return null
        val carpeta = File(context.filesDir, "fotos_reportes").apply { mkdirs() }
        val archivo = File(carpeta, "foto_$idReporte.jpg")
        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, salida)
        }
        Uri.fromFile(archivo).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}