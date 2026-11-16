package com.example.labfips.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF0B3C91)
private val AzulTitulo = Color(0xFF14213D)
private val Fondo = Color(0xFFEFF4F9)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val AzulClaroFondo = Color(0xFFE8F0FE)
private val Naranja = Color(0xFFF5A623)

@Composable
fun DetalleSeguimientoScreen(
    reporteId: String,
    onAtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reporte = reportesEstudianteMock.find { it.id == reporteId } ?: reportesEstudianteMock.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Fondo)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AzulClaroFondo)
                    .clickable { onAtras() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = AzulOscuro
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "Detalle del Reporte",
                    color = AzulTitulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reporte.ticket,
                    color = AzulOscuro,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, GrisBorde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ESTADO DEL SEGUIMIENTO",
                        color = GrisTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    PasosSeguimiento(pasoActual = reporte.pasoActual)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, GrisBorde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "INFORMACIÓN DEL EQUIPO",
                        color = GrisTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    DatoItem(label = "Código de Equipo", valor = reporte.codigoEquipo)
                    DatoItem(label = "Ubicación", valor = reporte.laboratorio)
                    DatoItem(label = "Categoría del daño", valor = reporte.tipoDanio)
                    DatoItem(label = "Fecha de envío", valor = "${reporte.fecha} a las ${reporte.hora}")

                    HorizontalDivider(color = GrisBorde, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Descripción del problema:",
                        color = AzulTitulo,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = reporte.descripcion,
                        color = GrisTexto,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    HorizontalDivider(color = GrisBorde, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Observaciones del encargado:",
                        color = Naranja,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = reporte.observacionesEncargado
                            ?: "El reporte aún no tiene observaciones asignadas por el encargado.",
                        color = if (reporte.observacionesEncargado != null) AzulTitulo else GrisTexto,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PasosSeguimiento(pasoActual: Int) {
    val pasos = listOf("1. Enviado", "2. En revisión", "3. Resuelto")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pasos.forEachIndexed { index, titulo ->
            val numPaso = index + 1
            val completado = numPaso <= pasoActual
            val esActual = numPaso == pasoActual
            val colorCirculo = when {
                completado && esActual && numPaso == 2 -> Naranja
                completado -> ColorExito
                else -> GrisBorde
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colorCirculo),
                    contentAlignment = Alignment.Center
                ) {
                    if (completado && !esActual) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "$numPaso",
                            color = if (completado) Color.White else GrisTexto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = titulo,
                    color = if (completado) AzulTitulo else GrisTexto,
                    fontSize = 12.sp,
                    fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun DatoItem(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = GrisTexto,
            fontSize = 13.sp
        )
        Text(
            text = valor,
            color = AzulTitulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
