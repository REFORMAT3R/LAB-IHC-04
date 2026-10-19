package com.example.labfips.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Paleta (misma que la pantalla 1 / Figma) ----------
private val AzulOscuro = Color(0xFF0B3C91)
private val AzulTitulo = Color(0xFF14213D)
private val Fondo = Color(0xFFEFF4F9)
private val GrisTexto = Color(0xFF6B7385)
private val GrisBorde = Color(0xFFD9DEE7)
private val Naranja = Color(0xFFF5A623)

// ---------- Modelo ----------
enum class TipoDanio(val etiqueta: String) {
    NO_ENCIENDE("PC no enciende"),
    MONITOR("Monitor"),
    TECLADO("Teclado"),
    MOUSE("Mouse"),
    RED("Red / Internet"),
    OTRO("Otro")
}

// ---------- Pantalla 2: Describir el daño ----------
@Composable
fun DescribirDanioScreen(
    codigoEquipo: String,
    tiposSeleccionados: Set<TipoDanio>,
    descripcion: String,
    fotoUri: Uri?,
    onTipoToggle: (TipoDanio) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onFotoChange: (Uri?) -> Unit,
    onAtras: () -> Unit,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
    ) {
        EncabezadoPaso(titulo = "Reportar equipo", pasoActual = 1)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Cuéntanos qué le pasa al equipo",
                color = GrisTexto,
                fontSize = 14.sp
            )

            EquipoIdentificado(codigoEquipo)

            SeccionTitulo("Tipo de daño")
            Text(
                text = "Puedes elegir más de uno",
                color = GrisTexto,
                fontSize = 12.sp
            )
            SelectorTipoDanio(tiposSeleccionados, onTipoToggle)

            SeccionTitulo("Descripción del problema")
            CampoDescripcion(descripcion, onDescripcionChange)

            SeccionTitulo("Evidencia (opcional)")
            AdjuntarFoto(fotoUri, onFotoChange)

            Spacer(Modifier.height(4.dp))
        }

        BotonesNavegacion(
            continuarHabilitado = tiposSeleccionados.isNotEmpty(),
            onAtras = onAtras,
            onContinuar = onContinuar
        )
        PasosInferiores(pasoActual = 1)
    }
}

// ---------- Composables independientes ----------
@Composable
fun EncabezadoPaso(titulo: String, pasoActual: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AzulOscuro),
            contentAlignment = Alignment.Center
        ) {
            Text("U", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = titulo,
            color = AzulTitulo,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(if (i == pasoActual) 22.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (i == pasoActual) AzulOscuro else GrisBorde)
                )
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(
        text = texto,
        color = AzulTitulo,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun EquipoIdentificado(codigo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, GrisBorde, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AzulOscuro)
        Spacer(Modifier.width(10.dp))
        Column {
            Text("Equipo identificado", color = GrisTexto, fontSize = 12.sp)
            Text(codigo, color = AzulTitulo, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Selección múltiple: cada toque activa o desactiva ese tipo de daño. */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectorTipoDanio(
    seleccionados: Set<TipoDanio>,
    onToggle: (TipoDanio) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TipoDanio.values().forEach { tipo ->
            val activo = tipo in seleccionados
            FilterChip(
                selected = activo,
                onClick = { onToggle(tipo) },
                label = { Text(tipo.etiqueta) },
                leadingIcon = if (activo) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null,
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = AzulOscuro,
                    labelColor = AzulTitulo,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Composable
fun CampoDescripcion(
    valor: String,
    onValorChange: (String) -> Unit,
    maxCaracteres: Int = 200
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { if (it.length <= maxCaracteres) onValorChange(it) },
        placeholder = { Text("Ej: El monitor no muestra imagen aunque la CPU está encendida") },
        supportingText = {
            Text("${valor.length}/$maxCaracteres", color = GrisTexto, fontSize = 11.sp)
        },
        minLines = 4,
        maxLines = 6,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AdjuntarFoto(
    fotoUri: Uri?,
    onFotoChange: (Uri?) -> Unit
) {
    val selector = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> onFotoChange(uri) }

    if (fotoUri == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, GrisBorde, RoundedCornerShape(14.dp))
                .clickable {
                    selector.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .padding(vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = "Adjuntar fotografía", tint = Naranja)
            Spacer(Modifier.height(6.dp))
            Text("Adjuntar fotografía", color = AzulTitulo, fontWeight = FontWeight.Medium)
            Text("Toca para elegir una imagen", color = GrisTexto, fontSize = 12.sp)
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, AzulOscuro, RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AzulOscuro)
            Spacer(Modifier.width(10.dp))
            Text("Fotografía adjuntada", color = AzulTitulo, modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.Close,
                contentDescription = "Quitar fotografía",
                tint = GrisTexto,
                modifier = Modifier.clickable { onFotoChange(null) }
            )
        }
    }
}

@Composable
fun BotonesNavegacion(
    continuarHabilitado: Boolean,
    onAtras: () -> Unit,
    onContinuar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onAtras,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AzulOscuro)
        ) { Text("Atrás", color = AzulOscuro) }

        Button(
            onClick = onContinuar,
            enabled = continuarHabilitado,
            modifier = Modifier
                .weight(2f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
        ) { Text("Continuar", color = Color.White, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
fun PasosInferiores(pasoActual: Int) {
    val pasos = listOf("① Escanear", "② Detallar", "③ Confirmar")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        pasos.forEachIndexed { i, texto ->
            Text(
                text = texto,
                fontSize = 11.sp,
                color = if (i == pasoActual) AzulTitulo else GrisTexto,
                fontWeight = if (i == pasoActual) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// ---------- Preview ----------
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360, heightDp = 780)
@Composable
private fun PreviewDescribirDanio() {
    DescribirDanioScreen(
        codigoEquipo = "PC-SISTEMAS-409",
        tiposSeleccionados = setOf(TipoDanio.MONITOR, TipoDanio.TECLADO),
        descripcion = "",
        fotoUri = null,
        onTipoToggle = {},
        onDescripcionChange = {},
        onFotoChange = {},
        onAtras = {},
        onContinuar = {}
    )
}