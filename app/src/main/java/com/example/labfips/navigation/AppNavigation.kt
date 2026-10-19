package com.example.labfips.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labfips.ui.ConfirmarScreen
import com.example.labfips.ui.DescribirDanioScreen
import com.example.labfips.ui.ReportarEquipoScreen
import com.example.labfips.ui.esCodigoValido
import com.example.labfips.ui.ReporteEnviadoDialog
import com.example.labfips.ui.TipoDanio
import java.time.LocalDate
import java.time.LocalTime

private object Rutas {
    const val ESCANEAR = "escanear"
    const val DETALLAR = "detallar"
    const val CONFIRMAR = "confirmar"
}

// ---------- Reglas de validación ----------
// El código del equipo (12 dígitos) se valida con esCodigoValido(), definida en ReportarEquipoScreen.kt
private const val MIN_DESCRIPCION = 10

// Ticket de ejemplo. TODO: reemplazar por el número real que devuelva el servidor/base de datos.
private fun generarTicket() = "#TI-${(1000..9999).random()}"

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current

    // ---- Datos del reporte, compartidos entre pantallas ----
    var codigo by rememberSaveable { mutableStateOf("") }
    var lab by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var hora by rememberSaveable { mutableStateOf<LocalTime?>(null) }
    var tipos by rememberSaveable { mutableStateOf<Set<TipoDanio>>(emptySet()) } // varios tipos de daño
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // ---- Estado de los cuadros de la pantalla 3 ----
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    var mostrarExito by rememberSaveable { mutableStateOf(false) }
    var ticket by rememberSaveable { mutableStateOf("") }

    fun aviso(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    NavHost(navController = nav, startDestination = Rutas.ESCANEAR) {

        // ---------- Pantalla 1: Escanear ----------
        composable(Rutas.ESCANEAR) {
            ReportarEquipoScreen(
                onScan = { c, l, f, h ->
                    val cod = c.trim()
                    when {
                        cod.isBlank() ->
                            aviso("Ingresa o escanea el código del equipo")
                        !esCodigoValido(cod) ->
                            aviso("Código no válido: debe tener exactamente 12 dígitos")
                        else -> {
                            codigo = cod
                            lab = l
                            fecha = f
                            hora = h
                            nav.navigate(Rutas.DETALLAR)
                        }
                    }
                }
            )
        }

        // ---------- Pantalla 2: Detallar ----------
        composable(Rutas.DETALLAR) {
            DescribirDanioScreen(
                codigoEquipo = codigo,
                tiposSeleccionados = tipos,
                descripcion = descripcion,
                fotoUri = fotoUri,
                // Cada toque activa o desactiva ese tipo
                onTipoToggle = { t -> tipos = if (t in tipos) tipos - t else tipos + t },
                onDescripcionChange = { descripcion = it },
                onFotoChange = { fotoUri = it },
                onAtras = { nav.popBackStack() },
                onContinuar = {
                    val desc = descripcion.trim()
                    val esOtro = TipoDanio.OTRO in tipos
                    when {
                        tipos.isEmpty() ->
                            aviso("Selecciona al menos un tipo de daño")
                        esOtro && desc.isEmpty() ->
                            aviso("Describe el daño para el tipo \"Otro\"")
                        desc.isNotEmpty() && desc.length < MIN_DESCRIPCION ->
                            aviso("La descripción debe tener al menos $MIN_DESCRIPCION caracteres")
                        else -> nav.navigate(Rutas.CONFIRMAR)
                    }
                },
                modifier = Modifier.statusBarsPadding().navigationBarsPadding().imePadding()
            )
        }

        // ---------- Pantalla 3: Confirmar ----------
        composable(Rutas.CONFIRMAR) {
            // Difumina la pantalla mientras se ve el cuadro de éxito (efecto visible en Android 12 o superior)
            val radioDesenfoque by animateDpAsState(
                targetValue = if (mostrarExito) 16.dp else 0.dp,
                label = "desenfoque"
            )

            ConfirmarScreen(
                codigoEquipo = codigo,
                ubicacion = lab,
                tiposSeleccionados = tipos,
                descripcion = descripcion,
                fotoUri = fotoUri,
                onAtras = { nav.popBackStack() },
                onEnviar = { mostrarConfirmacion = true },
                modifier = Modifier
                    .blur(radioDesenfoque)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            )

            // 1) Pregunta de confirmación
            if (mostrarConfirmacion) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacion = false },
                    title = { Text("¿Enviar reporte?") },
                    text = { Text("Una vez enviado no podrás modificarlo.") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacion = false
                            // TODO: aquí va el guardado/envío real del reporte
                            ticket = generarTicket()
                            mostrarExito = true
                        }) { Text("Enviar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacion = false }) { Text("Revisar") }
                    }
                )
            }

            // 2) Cuadro de éxito con el ticket
            if (mostrarExito) {
                ReporteEnviadoDialog(
                    ticket = ticket,
                    conFoto = fotoUri != null,
                    onListo = {
                        mostrarExito = false
                        // Limpiamos todo SOLO al pulsar "Listo", para que el resumen no quede vacío detrás del cuadro
                        codigo = ""; lab = ""; fecha = null; hora = null
                        tipos = emptySet(); descripcion = ""; fotoUri = null; ticket = ""
                        nav.navigate(Rutas.ESCANEAR) {
                            popUpTo(Rutas.ESCANEAR) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}