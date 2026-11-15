package com.example.labfips.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Paleta (la misma del resto de pantallas) ----------
private val AzulOscuro = Color(0xFF0B3C91)
private val AzulBoton = Color(0xFF1D5BCB)
private val AzulTitulo = Color(0xFF14213D)
private val Fondo = Color(0xFFEFF4F9)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val AzulClaroFondo = Color(0xFFE8F0FE)
private val NaranjaClaroFondo = Color(0xFFFFF1DC)
private val Naranja = Color(0xFFF5A623)

/**
 * Pantalla de inicio: el usuario elige cómo ingresar.
 * @param onEstudiante se llama al elegir "Estudiante" (va a Inicio del usuario)
 * @param onEncargado  se llama al elegir "Encargado" (va al Panel del encargado)
 */
@Composable
fun InicioScreen(
    onEstudiante: () -> Unit,
    onEncargado: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Marca de la app
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AzulOscuro),
                    contentAlignment = Alignment.Center
                ) {
                    Text("U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("LabFIPS", color = AzulTitulo, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Reporte de equipos de laboratorio", color = GrisTexto, fontSize = 13.sp)
                }
            }

            // Tarjeta de bienvenida
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(AzulOscuro, AzulBoton)))
            ) {
                // Círculos decorativos
                Canvas(Modifier.matchParentSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.07f),
                        radius = 110.dp.toPx(),
                        center = Offset(size.width, 0f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = 80.dp.toPx(),
                        center = Offset(0f, size.height)
                    )
                }
                Column(Modifier.padding(24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.DesktopWindows,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Reporta fallas de equipos en segundos",
                        color = Color.White,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Escanea el código del equipo, describe el problema y haz seguimiento de tu reporte.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Text(
                text = "¿Cómo deseas ingresar?",
                color = AzulTitulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            TarjetaRol(
                titulo = "Estudiante",
                descripcion = "Reporta un equipo dañado y revisa el estado de tus reportes.",
                icono = Icons.Outlined.School,
                colorIcono = AzulOscuro,
                fondoIcono = AzulClaroFondo,
                onClick = onEstudiante
            )

            TarjetaRol(
                titulo = "Encargado",
                descripcion = "Revisa los reportes recibidos y gestiona su estado.",
                icono = Icons.Outlined.AdminPanelSettings,
                colorIcono = Naranja,
                fondoIcono = NaranjaClaroFondo,
                onClick = onEncargado
            )
        }

        Text(
            text = "LabFIPS · Sistema de reporte de equipos",
            color = GrisTexto,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun TarjetaRol(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    fondoIcono: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, GrisBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, color = AzulTitulo, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(2.dp))
                Text(descripcion, color = GrisTexto, fontSize = 13.sp, lineHeight = 18.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GrisTexto)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewInicio() {
    InicioScreen(onEstudiante = {}, onEncargado = {})
}
