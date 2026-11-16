package com.example.labfips.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ---------- Paleta ----------
private val AzulOscuro = Color(0xFF0B3C91)
private val AzulBoton = Color(0xFF1D5BCB)
private val AzulTitulo = Color(0xFF14213D)
private val AzulClaroFondo = Color(0xFFE8F0FE)
private val Naranja = Color(0xFFF5A623)
private val NaranjaOscuro = Color(0xFFE8941A)
private val NaranjaClaroFondo = Color(0xFFFFF1DC)
private val Verde = Color(0xFF168049)
private val VerdeClaro = Color(0xFF1E9457)
private val VerdeFondo = Color(0xFFE6F4EA)
private val Fondo = Color(0xFFF3F5FA)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val TextoMensaje = Color(0xFF4B5563)

/** Colores e ícono propios de cada estado del reporte. */
private class TemaEstado(
    val principal: Color,
    val secundario: Color,
    val fondoClaro: Color,
    val icono: ImageVector
)

private fun temaDe(estado: String): TemaEstado = when (estado) {
    "Resuelto" -> TemaEstado(Verde, VerdeClaro, VerdeFondo, Icons.Outlined.CheckCircle)
    "En revisión" -> TemaEstado(AzulOscuro, AzulBoton, AzulClaroFondo, Icons.Outlined.RateReview)
    else -> TemaEstado(Naranja, NaranjaOscuro, NaranjaClaroFondo, Icons.Outlined.HourglassEmpty)
}

// =====================================================
//  1) Pregunta antes de guardar
// =====================================================

/**
 * "¿Guardar cambios?" con el cambio de estado a la vista (antes → después).
 */
@Composable
fun ConfirmarCambioDialog(
    ticket: String,
    estadoAnterior: String,
    estadoNuevo: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.padding(horizontal = 24.dp)) {
            TarjetaConfirmar(
                ticket = ticket,
                estadoAnterior = estadoAnterior,
                estadoNuevo = estadoNuevo,
                onConfirmar = onConfirmar,
                onCancelar = onCancelar
            )
        }
    }
}

@Composable
private fun TarjetaConfirmar(
    ticket: String,
    estadoAnterior: String,
    estadoNuevo: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val tema = temaDe(estadoNuevo)
    val cambioEstado = estadoAnterior != estadoNuevo

    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(tema.fondoClaro),
            contentAlignment = Alignment.Center
        ) {
            Icon(tema.icono, contentDescription = null, tint = tema.principal, modifier = Modifier.size(32.dp))
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "¿Guardar cambios?",
            color = AzulTitulo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Reporte $ticket",
            color = GrisTexto,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(16.dp))

        if (cambioEstado) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChipEstado(estadoAnterior)
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = GrisTexto,
                    modifier = Modifier.size(18.dp)
                )
                ChipEstado(estadoNuevo)
            }
        } else {
            Text(
                text = "Solo se actualizarán las observaciones.",
                color = AzulTitulo,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (cambioEstado && estadoNuevo == "Resuelto")
                "Al marcarlo como resuelto, el estudiante verá que el problema fue solucionado."
            else
                "El estudiante verá este cambio en su historial.",
            color = TextoMensaje,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Text("Cancelar", color = AzulTitulo)
            }
            Button(
                onClick = onConfirmar,
                modifier = Modifier
                    .weight(1.4f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tema.principal)
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ChipEstado(estado: String) {
    val tema = temaDe(estado)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(tema.fondoClaro)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = estado,
            color = tema.principal,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =====================================================
//  2) Mensaje de "cambios guardados" (cambia según el estado)
// =====================================================

/**
 * Mensaje de éxito después de guardar. El color, el título y el texto cambian según el estado:
 * Resuelto (verde), En revisión (azul), Pendiente (naranja) o solo observaciones.
 *
 * @param estado         estado en el que quedó el reporte
 * @param cambioEstado   true si el encargado cambió el estado (false = solo editó observaciones)
 * @param conObservacion true si dejó una observación para el estudiante
 */
@Composable
fun CambioGuardadoDialog(
    ticket: String,
    estado: String,
    cambioEstado: Boolean,
    conObservacion: Boolean,
    onListo: () -> Unit
) {
    Dialog(
        onDismissRequest = onListo,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(Modifier.padding(horizontal = 24.dp)) {
            TarjetaGuardado(
                ticket = ticket,
                estado = estado,
                cambioEstado = cambioEstado,
                conObservacion = conObservacion,
                onListo = onListo
            )
        }
    }
}

@Composable
private fun TarjetaGuardado(
    ticket: String,
    estado: String,
    cambioEstado: Boolean,
    conObservacion: Boolean,
    onListo: () -> Unit
) {
    val tema = temaDe(estado)

    val (titulo, subtitulo) = when {
        !cambioEstado -> "Observaciones guardadas" to "Tu mensaje ya está en el reporte."
        estado == "Resuelto" -> "¡Reporte resuelto!" to "El problema quedó solucionado."
        estado == "En revisión" -> "Reporte en revisión" to "Ya estás atendiendo este reporte."
        else -> "Reporte en espera" to "El reporte volvió a quedar pendiente."
    }

    val mensaje = when {
        !cambioEstado ->
            "El estudiante verá tus nuevas observaciones en el detalle de su reporte."
        estado == "Resuelto" ->
            "El estudiante verá que su reporte fue resuelto" +
                    (if (conObservacion) ", junto con tus observaciones," else "") +
                    " en su historial."
        estado == "En revisión" ->
            "El estudiante verá que su reporte está en revisión" +
                    (if (conObservacion) " y podrá leer tus observaciones" else " y podrá seguir su avance") +
                    " desde su historial."
        else ->
            "El estudiante verá su reporte como pendiente en su historial."
    }

    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
    ) {
        // ---------- Cabecera de color ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(tema.principal, tema.secundario)))
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.30f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        tema.icono,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitulo,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        // ---------- Cuerpo ----------
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Fondo)
                    .border(1.dp, GrisBorde, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REPORTE ACTUALIZADO",
                        color = GrisTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = ticket,
                        color = AzulOscuro,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                ChipEstado(estado)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = mensaje,
                color = TextoMensaje,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(tema.principal, tema.secundario)))
                    .clickable(onClick = onListo),
                contentAlignment = Alignment.Center
            ) {
                Text("Listo", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ---------- Previews ----------
@Preview(showBackground = true, backgroundColor = 0xFF888888, widthDp = 360, heightDp = 560)
@Composable
private fun PreviewConfirmar() {
    Box(Modifier.padding(24.dp)) {
        TarjetaConfirmar("#TI-4402", "Pendiente", "En revisión", {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF888888, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewGuardadoResuelto() {
    Box(Modifier.padding(24.dp)) {
        TarjetaGuardado("#TI-4402", "Resuelto", cambioEstado = true, conObservacion = true, onListo = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF888888, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewGuardadoRevision() {
    Box(Modifier.padding(24.dp)) {
        TarjetaGuardado("#TI-4402", "En revisión", cambioEstado = true, conObservacion = false, onListo = {})
    }
}