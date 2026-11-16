package com.example.labfips.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DesktopWindows
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
private val NaranjaClaroFondo = Color(0xFFFFF1DC)
private val VerdeFondo = Color(0xFFE6F4EA)

@Composable
fun HistorialScreen(
    onAtras: () -> Unit,
    onReporteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    contentDescription = "Volver",
                    tint = AzulOscuro
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "Mis Reportes",
                    color = AzulTitulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Historial y seguimiento de tus solicitudes",
                    color = GrisTexto,
                    fontSize = 12.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reportesEstudianteMock, key = { it.id }) { reporte ->
                TarjetaHistorialReporte(
                    reporte = reporte,
                    onClick = { onReporteClick(reporte.id) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaHistorialReporte(
    reporte: ReporteEstudiante,
    onClick: () -> Unit
) {
    val (colorEstado, fondoEstado) = when (reporte.estado.lowercase()) {
        "pendiente" -> Naranja to NaranjaClaroFondo
        "en revisión", "en revision" -> AzulOscuro to AzulClaroFondo
        "resuelto" -> ColorExito to VerdeFondo
        else -> GrisTexto to GrisBorde
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GrisBorde),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reporte.ticket,
                    color = AzulTitulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(fondoEstado)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reporte.estado,
                        color = colorEstado,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.DesktopWindows,
                    contentDescription = null,
                    tint = AzulOscuro,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${reporte.codigoEquipo} · ${reporte.laboratorio}",
                    color = AzulTitulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "Enviado el ${reporte.fecha} a las ${reporte.hora}",
                color = GrisTexto,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver detalle",
                    color = AzulOscuro,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AzulOscuro,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
