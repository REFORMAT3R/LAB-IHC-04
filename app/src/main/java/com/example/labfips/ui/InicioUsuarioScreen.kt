package com.example.labfips.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

/**
 * Menú principal del estudiante.
 * @param onNuevoReporte inicia el flujo: Identificación del equipo → Registro del daño → Confirmación
 * @param onMisReportes  va al Historial de reportes
 * @param onSalir        vuelve a la pantalla de inicio
 */
@Composable
fun InicioUsuarioScreen(
    onNuevoReporte: () -> Unit,
    onMisReportes: () -> Unit,
    onSalir: () -> Unit,
    nombreUsuario: String = "Estudiante",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        // ---------- Cabecera ----------
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
                    Text(
                        text = nombreUsuario.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Hola, $nombreUsuario",
                        color = AzulTitulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "¿Qué deseas hacer hoy?",
                        color = GrisTexto,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onSalir) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = GrisTexto
                    )
                }
            }
        }

        // ---------- Contenido ----------
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccionPrincipal(onClick = onNuevoReporte)

            TarjetaOpcion(
                titulo = "Mis reportes",
                descripcion = "Consulta el estado y el seguimiento de los reportes que enviaste.",
                onClick = onMisReportes
            )

            Text(
                text = "¿CÓMO FUNCIONA?",
                color = GrisTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GrisBorde),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PasoInfo(
                        numero = "1",
                        titulo = "Identifica el equipo",
                        detalle = "Escanea el código QR o de barras (12 dígitos) pegado al equipo."
                    )
                    PasoInfo(
                        numero = "2",
                        titulo = "Describe el daño",
                        detalle = "Elige el tipo de falla y agrega detalles o una foto."
                    )
                    PasoInfo(
                        numero = "3",
                        titulo = "Confirma y envía",
                        detalle = "Revisa el resumen. Recibirás un ticket para hacer seguimiento."
                    )
                }
            }
        }
    }
}

/** Tarjeta grande azul: la acción más importante (reportar un equipo). */
@Composable
private fun AccionPrincipal(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(AzulOscuro, AzulBoton)))
            .clickable(onClick = onClick)
    ) {
        Canvas(Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.07f),
                radius = 100.dp.toPx(),
                center = Offset(size.width, 0f)
            )
        }
        Column(Modifier.padding(22.dp)) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Reportar un equipo",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Escanea el código del equipo y cuéntanos qué falla.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Empezar", color = AzulOscuro, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AzulOscuro,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TarjetaOpcion(
    titulo: String,
    descripcion: String,
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
                    .background(AzulClaroFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.History,
                    contentDescription = null,
                    tint = AzulOscuro,
                    modifier = Modifier.size(28.dp)
                )
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

@Composable
private fun PasoInfo(numero: String, titulo: String, detalle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(AzulClaroFondo),
            contentAlignment = Alignment.Center
        ) {
            Text(numero, color = AzulOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, color = AzulTitulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(detalle, color = GrisTexto, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewInicioUsuario() {
    InicioUsuarioScreen(onNuevoReporte = {}, onMisReportes = {}, onSalir = {})
}