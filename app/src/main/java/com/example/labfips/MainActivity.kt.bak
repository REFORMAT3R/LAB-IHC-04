package com.example.labfips

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/*
 * Dependencias (build.gradle.kts del módulo app):
 *   implementation("androidx.compose.material:material-icons-extended")
 *   implementation("androidx.compose.material3:material3")
 *
 * Nota: LocalDate/LocalTime requieren minSdk 26 (o habilitar desugaring).
 * Para escanear de verdad: CameraX + ML Kit Barcode Scanning (ver ScannerPreview).
 */

// ---------- Colores ----------
private val Blue = Color(0xFF0A3D91)
private val BlueLight = Color(0xFF1D5BCB)
private val Background = Color(0xFFF3F5FA)
private val TextDark = Color(0xFF0F1B3D)
private val TextGray = Color(0xFF6B7280)
private val LabelGray = Color(0xFF8A94A8)
private val Border = Color(0xFFE1E6F0)
private val Amber = Color(0xFFF5A623)
private val ScanTop = Color(0xFF0E1B33)
private val ScanBottom = Color(0xFF173058)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme { ReportarEquipoScreen() }
        }
    }
}

// ---------- Pantalla ----------
@Composable
fun ReportarEquipoScreen(
    onScan: (codigo: String, lab: String, fecha: LocalDate, hora: LocalTime) -> Unit = { _, _, _, _ -> }
) {
    var codigo by rememberSaveable { mutableStateOf("") }
    var lab by rememberSaveable { mutableStateOf("Lab. Computación IV") }
    var fecha by remember { mutableStateOf(LocalDate.now()) }
    var hora by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }

    Scaffold(
        containerColor = Background,
        topBar = { Header() },
        bottomBar = {
            BottomAction(onClick = { onScan(codigo, lab, fecha, hora) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Apunta la cámara al código QR o de barras pegado al equipo para identificarlo.",
                color = TextGray, fontSize = 15.sp, lineHeight = 22.sp
            )

            ScannerPreview()

            ManualDivider()

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text("Ej: PC-SISTEMAS-409", color = Color(0xFF9CA3AF)) },
                leadingIcon = { Icon(Icons.Outlined.ViewWeek, null, tint = Color(0xFF9CA3AF)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Border,
                    focusedBorderColor = Blue
                )
            )
            Text(
                "El código está pegado al monitor o al CPU del equipo.",
                color = LabelGray, fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                "DATOS DEL REPORTE",
                color = LabelGray, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp
            )

            // Laboratorio
            FieldLabel("Laboratorio")
            LaboratorioDropdown(selected = lab, onSelected = { lab = it })

            // Fecha y hora
            val context = LocalContext.current
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Fecha")
                    FieldBox(
                        text = fecha.format(DateTimeFormatter.ofPattern("dd / MM / yyyy")),
                        trailingIcon = Icons.Outlined.CalendarToday,
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, y, m, d -> fecha = LocalDate.of(y, m + 1, d) },
                                fecha.year, fecha.monthValue - 1, fecha.dayOfMonth
                            ).show()
                        }
                    )
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Hora")
                    FieldBox(
                        text = hora.format(DateTimeFormatter.ofPattern("HH : mm")),
                        trailingIcon = Icons.Outlined.Schedule,
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m -> hora = LocalTime.of(h, m) },
                                hora.hour, hora.minute, true
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

// ---------- Cabecera ----------
@Composable
private fun Header() {
    Surface(color = Color.White, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Blue),
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Reportar equipo",
                color = TextDark, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            // Indicador de pasos (paso 1 de 3)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(width = 20.dp, height = 6.dp).clip(CircleShape).background(Blue))
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD5DAE5)))
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFD5DAE5)))
            }
        }
    }
}

// ---------- Visor del escáner ----------
@Composable
private fun ScannerPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(ScanTop, ScanBottom))),
        contentAlignment = Alignment.Center
    ) {
        // TODO: aquí va el PreviewView de CameraX (AndroidView) con ML Kit.
        Canvas(Modifier.matchParentSize()) {
            // Cuadrícula tenue
            val step = 22.dp.toPx()
            val gridColor = Color.White.copy(alpha = 0.05f)
            var x = 0f
            while (x < size.width) { drawLine(gridColor, Offset(x, 0f), Offset(x, size.height)); x += step }
            var y = 0f
            while (y < size.height) { drawLine(gridColor, Offset(0f, y), Offset(size.width, y)); y += step }

            // Esquinas del marco
            val fw = 175.dp.toPx()
            val fh = 125.dp.toPx()
            val l = (size.width - fw) / 2
            val t = (size.height - fh) / 2
            val r = l + fw
            val b = t + fh
            val len = 28.dp.toPx()
            val rad = 10.dp.toPx()
            val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)

            fun corner(build: Path.() -> Unit) = drawPath(Path().apply(build), Amber, style = stroke)

            corner { moveTo(l, t + len); lineTo(l, t + rad); quadraticTo(l, t, l + rad, t); lineTo(l + len, t) }
            corner { moveTo(r - len, t); lineTo(r - rad, t); quadraticTo(r, t, r, t + rad); lineTo(r, t + len) }
            corner { moveTo(l, b - len); lineTo(l, b - rad); quadraticTo(l, b, l + rad, b); lineTo(l + len, b) }
            corner { moveTo(r - len, b); lineTo(r - rad, b); quadraticTo(r, b, r, b - rad); lineTo(r, b - len) }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.QrCode2, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(6.dp))
            Text("Centra el código aquí", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}

// ---------- Separador "O INGRESAR MANUALMENTE" ----------
@Composable
private fun ManualDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = Border)
        Text(
            "O INGRESAR MANUALMENTE",
            modifier = Modifier.padding(horizontal = 12.dp),
            color = LabelGray, fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp
        )
        HorizontalDivider(Modifier.weight(1f), color = Border)
    }
}

// ---------- Componentes de formulario ----------
@Composable
private fun FieldLabel(text: String) {
    Text(text, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
}

/** Campo tipo "caja" clicable (laboratorio, fecha, hora). */
@Composable
private fun FieldBox(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(if (highlighted) 1.5.dp else 1.dp, if (highlighted) Blue else Border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, null, tint = TextGray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
            }
            Text(text, color = TextDark, fontSize = 15.sp, modifier = Modifier.weight(1f))
            if (trailingIcon != null) {
                Icon(trailingIcon, null, tint = TextDark, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun LaboratorioDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val labs = listOf(
        "Lab. Computación I", "Lab. Computación II", "Lab. Computación III",
        "Lab. Computación IV", "Lab. Redes", "Lab. Electrónica"
    )
    Box {
        FieldBox(
            text = selected,
            leadingIcon = Icons.Outlined.DesktopWindows,
            highlighted = true,
            onClick = { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            labs.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = { onSelected(item); expanded = false }
                )
            }
        }
    }
}

// ---------- Barra inferior ----------
@Composable
private fun BottomAction(onClick: () -> Unit) {
    Surface(color = Background, shadowElevation = 0.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Blue, BlueLight)))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.QrCodeScanner, null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text("Escanear código", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("① Escanear" to true, "② Detallar" to false, "③ Confirmar" to false).forEach { (label, active) ->
                    Text(
                        label, fontSize = 12.sp,
                        color = if (active) TextDark else LabelGray,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ReportarEquipoPreview() {
    MaterialTheme { ReportarEquipoScreen() }
}