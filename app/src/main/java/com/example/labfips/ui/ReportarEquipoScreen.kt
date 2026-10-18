package com.example.labfips.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

// ---------- Colores (privados de este archivo) ----------
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

// ---------- Pantalla 1: Escanear ----------
@Composable
fun ReportarEquipoScreen(
    onScan: (codigo: String, lab: String, fecha: LocalDate, hora: LocalTime) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current

    var codigo by rememberSaveable { mutableStateOf("") }
    var lab by rememberSaveable { mutableStateOf("Lab. Computación IV") }
    var fecha by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var hora by rememberSaveable { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
    var escaneando by remember { mutableStateOf(false) }

    // Permiso de cámara
    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            escaneando = true
        } else {
            Toast.makeText(
                context,
                "Necesito el permiso de cámara para escanear. También puedes escribir el código.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Texto e ícono del botón principal según el estado
    val (textoBoton, iconoBoton) = when {
        escaneando -> "Detener cámara" to Icons.Outlined.Close
        codigo.isNotBlank() -> "Continuar" to Icons.AutoMirrored.Filled.ArrowForward
        else -> "Escanear código" to Icons.Outlined.QrCodeScanner
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Box(Modifier.background(Color.White).statusBarsPadding()) {
                EncabezadoPaso(titulo = "Reportar equipo", pasoActual = 0)
            }
        },
        bottomBar = {
            BottomAction(
                texto = textoBoton,
                icono = iconoBoton,
                onClick = {
                    when {
                        escaneando -> escaneando = false
                        codigo.isNotBlank() -> onScan(codigo, lab, fecha, hora)
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                                PackageManager.PERMISSION_GRANTED -> escaneando = true
                        else -> permisoCamara.launch(Manifest.permission.CAMERA)
                    }
                }
            )
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

            ScannerPreview(
                escaneando = escaneando,
                onCodigo = { valor ->
                    codigo = valor.trim()
                    escaneando = false
                    Toast.makeText(context, "Código detectado: $codigo", Toast.LENGTH_SHORT).show()
                }
            )

            ManualDivider()

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text("Ej: PC-SISTEMAS-409", color = Color(0xFF9CA3AF)) },
                leadingIcon = { Icon(Icons.Outlined.ViewWeek, null, tint = Color(0xFF9CA3AF)) },
                trailingIcon = {
                    if (codigo.isNotEmpty()) {
                        IconButton(onClick = { codigo = "" }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Borrar código", tint = TextGray)
                        }
                    }
                },
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
                textAlign = TextAlign.Center
            )

            Text(
                "DATOS DEL REPORTE",
                color = LabelGray, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp
            )

            FieldLabel("Laboratorio")
            LaboratorioDropdown(selected = lab, onSelected = { lab = it })

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

// ---------- Visor del escáner (cámara real) ----------
@Composable
private fun ScannerPreview(
    escaneando: Boolean,
    onCodigo: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(ScanTop, ScanBottom))),
        contentAlignment = Alignment.Center
    ) {
        // Cámara (solo cuando se está escaneando)
        if (escaneando) {
            CamaraEscaner(modifier = Modifier.matchParentSize(), onCodigo = onCodigo)
        }

        // Marco y cuadrícula
        Canvas(Modifier.matchParentSize()) {
            if (!escaneando) {
                val step = 22.dp.toPx()
                val gridColor = Color.White.copy(alpha = 0.05f)
                var x = 0f
                while (x < size.width) { drawLine(gridColor, Offset(x, 0f), Offset(x, size.height)); x += step }
                var y = 0f
                while (y < size.height) { drawLine(gridColor, Offset(0f, y), Offset(size.width, y)); y += step }
            }

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

        if (!escaneando) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.QrCode2, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(6.dp))
                Text("Centra el código aquí", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}

/** Cámara trasera + lectura de QR / códigos de barras con ML Kit. */
@Composable
private fun CamaraEscaner(
    modifier: Modifier,
    onCodigo: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner // la Activity es el LifecycleOwner
    val onCodigoActual by rememberUpdatedState(onCodigo)

    val previewView = remember {
        PreviewView(context).apply {
            // COMPATIBLE permite recortar con esquinas redondeadas
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(Unit) {
        val executor = Executors.newSingleThreadExecutor()
        val scanner = BarcodeScanning.getClient()
        val entregado = AtomicBoolean(false)
        val futuro = ProcessCameraProvider.getInstance(context)
        var provider: ProcessCameraProvider? = null

        futuro.addListener({
            val p = futuro.get()
            provider = p

            val vista = CameraXPreview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analisis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analisis.setAnalyzer(executor) { proxy ->
                analizarImagen(proxy, scanner) { valor ->
                    // Solo entregamos el primer código detectado
                    if (entregado.compareAndSet(false, true)) onCodigoActual(valor)
                }
            }

            try {
                p.unbindAll()
                p.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, vista, analisis)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            provider?.unbindAll()
            executor.execute { scanner.close() }
            executor.shutdown()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun analizarImagen(
    proxy: ImageProxy,
    scanner: BarcodeScanner,
    onValor: (String) -> Unit
) {
    val media = proxy.image
    if (media == null) {
        proxy.close()
        return
    }
    val imagen = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
    scanner.process(imagen)
        .addOnSuccessListener { codigos ->
            codigos.firstNotNullOfOrNull { it.rawValue }?.let(onValor)
        }
        .addOnCompleteListener { proxy.close() }
}

// ---------- Separador ----------
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
private fun BottomAction(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Surface(color = Background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Blue, BlueLight)))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icono, null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text(texto, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(10.dp))
            PasosInferiores(pasoActual = 0)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ReportarEquipoPreview() {
    MaterialTheme { ReportarEquipoScreen() }
}