package com.example.labfips.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ---------- Paleta ----------
private val Verde = Color(0xFF168049)
private val VerdeClaro = Color(0xFF1E9457)
private val AzulOscuro = Color(0xFF0B3C91)
private val AzulBoton = Color(0xFF1D5BCB)
private val AzulClaroFondo = Color(0xFFE8F0FE)
private val Fondo = Color(0xFFF3F5FA)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val TextoMensaje = Color(0xFF4B5563)

/**
 * Cuadro de "Reporte enviado con éxito".
 * @param ticket  número que se muestra, por ejemplo "#TI-4402"
 * @param conFoto true si el reporte llevaba fotografía (cambia el texto del mensaje)
 * @param onListo se llama al pulsar "Listo" (o el botón atrás del celular)
 */
@Composable
fun ReporteEnviadoDialog(
    ticket: String,
    conFoto: Boolean,
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
            TarjetaExito(ticket = ticket, conFoto = conFoto, onListo = onListo)
        }
    }
}

@Composable
private fun TarjetaExito(
    ticket: String,
    conFoto: Boolean,
    onListo: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
    ) {
        // ---------- Cabecera verde ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Verde, VerdeClaro)))
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
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "¡Reporte Enviado con Éxito!",
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tu solicitud ha quedado registrada.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        // ---------- Cuerpo blanco ----------
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta del ticket
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
                        text = "TICKET GENERADO",
                        color = GrisTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = ticket,
                        color = AzulOscuro,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AzulClaroFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = AzulOscuro)
                }
            }

            Spacer(Modifier.height(16.dp))

            val detalle = if (conFoto) "su reporte junto con la fotografía de evidencia" else "su reporte"
            Text(
                text = "El encargado del laboratorio ha recibido $detalle y procederá a revisar y verificar el equipo dañado a la brevedad.",
                color = TextoMensaje,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // Botón "Listo"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(AzulOscuro, AzulBoton)))
                    .clickable(onClick = onListo),
                contentAlignment = Alignment.Center
            ) {
                Text("Listo", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF888888, widthDp = 360, heightDp = 640)
@Composable
private fun PreviewReporteEnviado() {
    Box(Modifier.padding(24.dp)) {
        TarjetaExito(ticket = "#TI-4402", conFoto = true, onListo = {})
    }
}