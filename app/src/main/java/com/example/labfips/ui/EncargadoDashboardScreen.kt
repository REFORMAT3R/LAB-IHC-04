package com.example.labfips.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF0B3C91)
private val AzulBoton = Color(0xFF1D5BCB)
private val AzulTitulo = Color(0xFF14213D)
private val Fondo = Color(0xFFEFF4F9)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val AzulClaroFondo = Color(0xFFE8F0FE)
private val Naranja = Color(0xFFF5A623)
private val NaranjaClaroFondo = Color(0xFFFFF1DC)
private val VerdeFondo = Color(0xFFE6F4EA)

/**
 * Panel del encargado: resumen de reportes y acceso a la lista.
 */
@Composable
fun EncargadoDashboardScreen(
    onVerReportes: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendientes = reportesMock.count { it.estado.equals("Pendiente", ignoreCase = true) }
    val enRevision = reportesMock.count { it.estado.equals("En revisión", ignoreCase = true) }
    val resueltos = reportesMock.count { it.estado.equals("Resuelto", ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AzulOscuro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Panel del Encargado",
                        color = AzulTitulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "LabFIPS · Gestión de reportes",
                        color = GrisTexto,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onVolver) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = GrisTexto
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Hola, Encargado",
                color = AzulTitulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Revisa el estado de los equipos reportados y da seguimiento.",
                color = GrisTexto,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(AzulOscuro, AzulBoton)))
            ) {
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.07f),
                        radius = 100.dp.toPx(),
                        center = Offset(size.width, 0f)
                    )
                }
                Column(Modifier.padding(22.dp)) {
                    Text(
                        text = "Tienes $pendientes reportes pendientes",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Hay ${reportesMock.size} reportes en total. Entra a la lista para gestionarlos.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GrisBorde),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Resumen",
                        color = AzulTitulo,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    ContadorEstado(
                        titulo = "Pendientes",
                        cantidad = pendientes,
                        icono = Icons.Outlined.HourglassEmpty,
                        colorIcono = Naranja,
                        fondoIcono = NaranjaClaroFondo
                    )
                    ContadorEstado(
                        titulo = "En revisión",
                        cantidad = enRevision,
                        icono = Icons.Outlined.RateReview,
                        colorIcono = AzulOscuro,
                        fondoIcono = AzulClaroFondo
                    )
                    ContadorEstado(
                        titulo = "Resueltos",
                        cantidad = resueltos,
                        icono = Icons.Outlined.CheckCircle,
                        colorIcono = ColorExito,
                        fondoIcono = VerdeFondo
                    )
                }
            }

            Button(
                onClick = onVerReportes,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    Icons.Outlined.Assignment,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text("Ver Lista de Reportes", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ContadorEstado(
    titulo: String,
    cantidad: Int,
    icono: ImageVector,
    colorIcono: Color,
    fondoIcono: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(fondoIcono),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = titulo,
            color = AzulTitulo,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = cantidad.toString(),
            color = AzulTitulo,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewEncargadoDashboard() {
    EncargadoDashboardScreen(onVerReportes = {}, onVolver = {})
}
