package com.example.labfips.ui

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val AzulOscuro = Color(0xFF0B3C91)
private val AzulTitulo = Color(0xFF14213D)
private val Fondo = Color(0xFFEFF4F9)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val AzulClaroFondo = Color(0xFFE8F0FE)
private val Naranja = Color(0xFFF5A623)
private val NaranjaClaroFondo = Color(0xFFFFF1DC)
private val VerdeFondo = Color(0xFFE6F4EA)

// ---------- Reglas ----------
private const val MIN_OBSERVACION = 10
private const val MAX_OBSERVACION = 300

/** Estados posibles del reporte (los mismos textos que usan Historial y Lista de reportes). */
private val ESTADOS = listOf(
    "Pendiente" to "Aún no se ha revisado el equipo.",
    "En revisión" to "Un técnico está atendiendo el problema.",
    "Resuelto" to "El problema ya fue solucionado."
)

/** Paso del seguimiento que ve el estudiante según el estado. */
private fun pasoDeEstado(estado: String): Int = when (estado) {
    "Resuelto" -> 3
    "En revisión" -> 2
    else -> 1
}

private fun colorDeEstado(estado: String): Pair<Color, Color> = when (estado.lowercase()) {
    "pendiente" -> Naranja to NaranjaClaroFondo
    "en revisión", "en revision" -> AzulOscuro to AzulClaroFondo
    "resuelto" -> ColorExito to VerdeFondo
    else -> GrisTexto to GrisBorde
}

/** Texto del error de las observaciones, o null si está bien. */
private fun errorDeObservaciones(estado: String, observaciones: String): String? {
    val o = observaciones.trim()
    return when {
        estado == "Resuelto" && o.isEmpty() ->
            "Explica cómo se resolvió el problema para poder marcarlo como resuelto"
        o.isNotEmpty() && o.length < MIN_OBSERVACION ->
            "Escribe al menos $MIN_OBSERVACION caracteres (te faltan ${MIN_OBSERVACION - o.length})"
        else -> null
    }
}

/**
 * Guarda el cambio en las dos listas compartidas, para que el encargado y el estudiante
 * vean lo mismo: estado y observaciones (el reporte se busca por su id).
 */
private fun aplicarCambios(id: String, estado: String, observaciones: String) {
    val obs = observaciones.trim().ifEmpty { null }

    val i = reportesMock.indexOfFirst { it.id == id }
    if (i >= 0) reportesMock[i] = reportesMock[i].copy(estado = estado)

    val j = reportesEstudianteMock.indexOfFirst { it.id == id }
    if (j >= 0) {
        reportesEstudianteMock[j] = reportesEstudianteMock[j].copy(
            estado = estado,
            pasoActual = pasoDeEstado(estado),
            observacionesEncargado = obs
        )
    }
}

/**
 * Gestión del reporte (Integrante 4): el encargado revisa un reporte,
 * cambia su estado y agrega observaciones para el estudiante.
 */
@Composable
fun GestionReporteScreen(
    reporteId: String,
    onAtras: () -> Unit,
    onGuardado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reporte = reportesMock.find { it.id == reporteId }
    val detalleEstudiante = reportesEstudianteMock.find { it.id == reporteId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .imePadding()
    ) {
        Encabezado(subtitulo = reporte?.ticket, onAtras = onAtras)

        if (reporte == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontró el reporte. Vuelve a la lista e inténtalo de nuevo.",
                    color = GrisTexto,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Formulario(
                reporte = reporte,
                detalle = detalleEstudiante,
                onGuardado = onGuardado,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Encabezado(subtitulo: String?, onAtras: () -> Unit) {
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
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = AzulOscuro
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = "Gestionar reporte",
                color = AzulTitulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitulo != null) {
                Text(text = subtitulo, color = GrisTexto, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun Formulario(
    reporte: ReporteEncargado,
    detalle: ReporteEstudiante?,
    onGuardado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val observacionInicial = detalle?.observacionesEncargado.orEmpty()

    var estadoSel by rememberSaveable(reporte.id) { mutableStateOf(reporte.estado) }
    var observaciones by rememberSaveable(reporte.id) { mutableStateOf(observacionInicial) }

    // Los errores solo se muestran después de intentar guardar y desaparecen al corregirlos
    var intento by remember { mutableStateOf(false) }
    var confirmar by remember { mutableStateOf(false) }
    var exito by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    val errorObservaciones = if (intento) errorDeObservaciones(estadoSel, observaciones) else null
    val sinCambios = estadoSel == reporte.estado && observaciones.trim() == observacionInicial.trim()
    val errorGeneral =
        if (intento && errorObservaciones == null && sinCambios)
            "No hiciste ningún cambio: cambia el estado o escribe una observación."
        else null

    val guardar: () -> Unit = {
        intento = true
        if (errorDeObservaciones(estadoSel, observaciones) != null) {
            // El campo de observaciones está al final: bajamos para que se vea el error
            scope.launch { scroll.animateScrollTo(scroll.maxValue) }
        } else if (!sinCambios) {
            confirmar = true
        }
    }

    val textoConfirmacion =
        if (estadoSel != reporte.estado)
            "El reporte ${reporte.ticket} pasará de «${reporte.estado}» a «$estadoSel» y el estudiante lo verá en su historial."
        else
            "Se actualizarán las observaciones del reporte ${reporte.ticket}."

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---------- Datos del reporte ----------
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GrisBorde),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val (colorEstado, fondoEstado) = colorDeEstado(reporte.estado)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reporte.ticket,
                            color = AzulTitulo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
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

                    InfoFila(Icons.Outlined.DesktopWindows, "${reporte.codigoEquipo} · ${reporte.laboratorio}")
                    InfoFila(
                        Icons.Outlined.Schedule,
                        if (detalle != null) "${reporte.fecha} · ${detalle.hora}" else reporte.fecha
                    )
                    if (detalle != null) {
                        InfoFila(Icons.Outlined.Build, detalle.tipoDanio)
                    }

                    HorizontalDivider(color = GrisBorde)

                    Text(
                        text = "DESCRIPCIÓN DEL ESTUDIANTE",
                        color = GrisTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = reporte.descripcion,
                        color = AzulTitulo,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    HorizontalDivider(color = GrisBorde)

                    Text(
                        text = "FOTOGRAFÍA DE EVIDENCIA",
                        color = GrisTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (reporte.fotoUri != null) {
                        VistaPreviaImagen(
                            uri = Uri.parse(reporte.fotoUri),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Text(
                            text = "El estudiante no adjuntó fotografía.",
                            color = GrisTexto,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ---------- Estado ----------
            Titulo("Estado del reporte")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ESTADOS.forEach { (nombre, detalleEstado) ->
                    OpcionEstado(
                        titulo = nombre,
                        detalle = detalleEstado,
                        color = colorDeEstado(nombre).first,
                        seleccionado = estadoSel == nombre,
                        onClick = { estadoSel = nombre }
                    )
                }
            }

            // ---------- Observaciones ----------
            Titulo(
                texto = if (estadoSel == "Resuelto") "Observaciones para el estudiante (obligatorio)"
                else "Observaciones para el estudiante (opcional)",
                conError = errorObservaciones != null
            )
            OutlinedTextField(
                value = observaciones,
                onValueChange = { if (it.length <= MAX_OBSERVACION) observaciones = it },
                placeholder = { Text("Ej: Se reemplazó el cable HDMI y el monitor funciona correctamente.") },
                isError = errorObservaciones != null,
                supportingText = {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = errorObservaciones ?: "",
                            color = ColorError,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${observaciones.length}/$MAX_OBSERVACION",
                            color = if (observaciones.length >= MAX_OBSERVACION) ColorError else GrisTexto,
                            fontSize = 11.sp
                        )
                    }
                },
                minLines = 4,
                maxLines = 6,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(errorBorderColor = ColorError),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "El estudiante verá el nuevo estado y tus observaciones en su historial.",
                color = GrisTexto,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(4.dp))
        }

        // ---------- Barra inferior ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Fondo)
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MensajeError(errorGeneral)
            Button(
                onClick = guardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Guardar cambios",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }

    // ---------- Confirmación y éxito ----------
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Guardar cambios?") },
            text = { Text(textoConfirmacion) },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    aplicarCambios(reporte.id, estadoSel, observaciones)
                    exito = true
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text("Cancelar") }
            }
        )
    }

    if (exito) {
        AlertDialog(
            onDismissRequest = { exito = false; onGuardado() },
            title = { Text("Cambios guardados") },
            text = { Text("El estudiante ya puede ver el nuevo estado y tus observaciones en su historial.") },
            confirmButton = {
                TextButton(onClick = { exito = false; onGuardado() }) { Text("Listo") }
            }
        )
    }
}

@Composable
private fun Titulo(texto: String, conError: Boolean = false) {
    Text(
        text = texto,
        color = if (conError) ColorError else AzulTitulo,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun InfoFila(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = texto, color = AzulTitulo, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun OpcionEstado(
    titulo: String,
    detalle: String,
    color: Color,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(if (seleccionado) 2.dp else 1.dp, if (seleccionado) color else GrisBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = seleccionado,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = color)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, color = AzulTitulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(detalle, color = GrisTexto, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewGestionReporte() {
    GestionReporteScreen(reporteId = "1", onAtras = {}, onGuardado = {})
}