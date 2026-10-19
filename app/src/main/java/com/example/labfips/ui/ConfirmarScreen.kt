package com.example.labfips.ui

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Paleta heredada y extendida ----------
private val AzulOscuro = Color(0xFF0B3C91)
private val AzulTitulo = Color(0xFF14213D)
private val Fondo = Color(0xFFEFF4F9)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val AzulClaroFondo = Color(0xFFE8F0FE)

// Colores para la alerta amarilla
private val AmarilloAvisoFondo = Color(0xFFFFF9E6)
private val AmarilloAvisoBorde = Color(0xFFFFD54F)
private val AmarilloAvisoTexto = Color(0xFF9E7C0C)

@Composable
fun ConfirmarScreen(
    codigoEquipo: String,
    ubicacion: String,
    tiposSeleccionados: Set<TipoDanio>,
    descripcion: String,
    fotoUri: Uri?,
    onAtras: () -> Unit,
    onEnviar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        // TopBar adaptado al diseño de la imagen
        EncabezadoConfirmacion(onAtras = onAtras)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta principal blanca
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GrisBorde),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Cabecera de la tarjeta
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AzulOscuro)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resumen del Reporte",
                            color = AzulOscuro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    HorizontalDivider(color = GrisBorde, thickness = 1.dp)

                    // Sección: Equipo
                    SeccionResumen(titulo = "EQUIPO") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AzulClaroFondo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DesktopMac, contentDescription = null, tint = AzulOscuro)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = codigoEquipo,
                                    color = AzulTitulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = ubicacion,
                                    color = GrisTexto,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = GrisBorde, thickness = 1.dp)

                    // Sección: Problema
                    SeccionResumen(titulo = if (tiposSeleccionados.size > 1) "PROBLEMAS SELECCIONADOS" else "PROBLEMA SELECCIONADO") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (tiposSeleccionados.isEmpty()) {
                                Text(
                                    text = "No especificado",
                                    color = GrisTexto,
                                    fontSize = 15.sp
                                )
                            } else {
                                tiposSeleccionados.forEach { tipo ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DesktopMac, contentDescription = null, tint = AzulTitulo)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = tipo.etiqueta,
                                            color = AzulTitulo,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = GrisBorde, thickness = 1.dp)

                    // Sección: Detalles
                    SeccionResumen(titulo = "DETALLES ADICIONALES") {
                        Text(
                            text = descripcion.ifBlank { "Sin detalles adicionales." },
                            color = AzulTitulo,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    HorizontalDivider(color = GrisBorde, thickness = 1.dp)

                    // Sección: Evidencia
                    SeccionResumen(titulo = "FOTOGRAFÍA DE EVIDENCIA") {
                        if (fotoUri == null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ImageNotSupported,
                                    contentDescription = null,
                                    tint = GrisTexto,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "No se adjuntó ninguna fotografía",
                                    color = GrisTexto,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Text(
                                text = "Fotografía adjunta correctamente.",
                                color = AzulTitulo,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Caja de advertencia amarilla
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmarilloAvisoFondo)
                    .border(1.dp, AmarilloAvisoBorde, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = AmarilloAvisoTexto,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Una vez enviado, el reporte no puede modificarse. Revisa los datos antes de continuar.",
                        color = AmarilloAvisoTexto,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Botón inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Fondo)
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 10.dp)
        ) {
            Button(
                onClick = onEnviar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar reporte", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }

        // Barra de pasos inferior (① Escanear · ② Detallar · ③ Confirmar)
        PasosInferiores(pasoActual = 2)
    }
}

// ---------- Componentes de UI Internos ----------

@Composable
fun EncabezadoConfirmacion(onAtras: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Fondo)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón atrás
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AzulClaroFondo)
                .clickable { onAtras() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = AzulOscuro)
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = "Confirmar reporte",
            color = AzulTitulo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        // Indicador de pasos (2 puntos grises, 1 línea azul)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GrisBorde))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GrisBorde))
            Box(modifier = Modifier.height(6.dp).width(16.dp).clip(CircleShape).background(AzulOscuro))
        }
    }
}

@Composable
fun SeccionResumen(titulo: String, contenido: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = titulo,
            color = GrisTexto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        contenido()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewConfirmarScreen() {
    ConfirmarScreen(
        codigoEquipo = "PC-SISTEMAS-409",
        ubicacion = "Lab. Computación IV",
        tiposSeleccionados = setOf(TipoDanio.MONITOR, TipoDanio.TECLADO),
        descripcion = "La pantalla parpadea en negro constantemente al encender el equipo.",
        fotoUri = null,
        onAtras = {},
        onEnviar = {}
    )
}