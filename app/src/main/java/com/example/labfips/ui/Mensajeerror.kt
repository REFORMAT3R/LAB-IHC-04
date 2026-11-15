package com.example.labfips.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Rojo que se usa en todos los indicadores de error de la app. */
val ColorError = Color(0xFFD93025)

/** Verde para los indicadores de "todo bien". */
val ColorExito = Color(0xFF168049)

/**
 * Mensaje de error que se pone DEBAJO de un campo (ícono rojo + texto).
 * Si [texto] es null no se muestra nada.
 */
@Composable
fun MensajeError(
    texto: String?,
    modifier: Modifier = Modifier
) {
    if (texto == null) return
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = ColorError,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = ColorError,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}