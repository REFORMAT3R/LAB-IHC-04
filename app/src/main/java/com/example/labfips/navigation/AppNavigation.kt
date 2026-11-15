package com.example.labfips.navigation

import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labfips.ui.ConfirmarScreen
import com.example.labfips.ui.DescribirDanioScreen
import com.example.labfips.ui.InicioScreen
import com.example.labfips.ui.InicioUsuarioScreen
import com.example.labfips.ui.PantallaPendiente
import com.example.labfips.ui.ReportarEquipoScreen
import com.example.labfips.ui.ReporteEnviadoDialog
import com.example.labfips.ui.TipoDanio
import java.time.LocalDate
import java.time.LocalTime

/**
 * Rutas de todo el sistema. Cada integrante agrega aquí SOLO las suyas.
 *
 * Usuario:   INICIO → INICIO_USUARIO → ESCANEAR → DETALLAR → CONFIRMAR → HISTORIAL → (detalle)
 * Encargado: INICIO → ENCARGADO → (lista de reportes) → (gestión del reporte)
 */
private object Rutas {
    // Integrante 1
    const val INICIO = "inicio"
    const val INICIO_USUARIO = "inicio_usuario"

    // Flujo de reporte (ya existente)
    const val ESCANEAR = "escanear"
    const val DETALLAR = "detallar"
    const val CONFIRMAR = "confirmar"

    // Integrante 2
    const val HISTORIAL = "historial"
    // TODO Integrante 2: const val DETALLE = "detalle/{id}"

    // Integrante 3
    const val ENCARGADO = "encargado"
    // TODO Integrante 3: const val LISTA_REPORTES = "lista_reportes"

    // TODO Integrante 4: const val GESTION = "gestion/{id}"
}

// Ticket de ejemplo. TODO: reemplazar por el número real que devuelva el servidor/base de datos.
private fun generarTicket() = "#TI-${(1000..9999).random()}"

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    // ---- Datos del reporte que se está llenando, compartidos entre pantallas ----
    var codigo by rememberSaveable { mutableStateOf("") }
    var lab by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var hora by rememberSaveable { mutableStateOf<LocalTime?>(null) }
    var tipos by rememberSaveable { mutableStateOf<Set<TipoDanio>>(emptySet()) } // varios tipos de daño
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // ---- Estado de los cuadros de la pantalla de confirmación ----
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    var mostrarExito by rememberSaveable { mutableStateOf(false) }
    var ticket by rememberSaveable { mutableStateOf("") }

    /** Deja en blanco el reporte en curso (para empezar uno nuevo o al terminar de enviar). */
    fun limpiarReporte() {
        codigo = ""; lab = ""; fecha = null; hora = null
        tipos = emptySet(); descripcion = ""; fotoUri = null; ticket = ""
        mostrarConfirmacion = false; mostrarExito = false
    }

    // Evita que un doble toque navegue dos veces (o saque al usuario de la app sin querer)
    fun puedeNavegar() =
        nav.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

    fun irA(ruta: String) {
        if (puedeNavegar()) nav.navigate(ruta)
    }

    fun volver() {
        if (puedeNavegar()) nav.popBackStack()
    }

    NavHost(navController = nav, startDestination = Rutas.INICIO) {

        // =====================================================
        //  INTEGRANTE 1: Inicio + Inicio del usuario
        // =====================================================

        // ---------- Pantalla de inicio ----------
        composable(Rutas.INICIO) {
            InicioScreen(
                onEstudiante = { irA(Rutas.INICIO_USUARIO) },
                onEncargado = { irA(Rutas.ENCARGADO) }
            )
        }

        // ---------- Inicio del usuario (menú del estudiante) ----------
        composable(Rutas.INICIO_USUARIO) {
            InicioUsuarioScreen(
                onNuevoReporte = {
                    limpiarReporte() // cada reporte nuevo empieza vacío
                    irA(Rutas.ESCANEAR)
                },
                onMisReportes = { irA(Rutas.HISTORIAL) },
                onSalir = {
                    if (puedeNavegar()) {
                        nav.navigate(Rutas.INICIO) { popUpTo(Rutas.INICIO) { inclusive = true } }
                    }
                }
            )
        }

        // =====================================================
        //  FLUJO DE REPORTE (ya existente)
        // =====================================================

        // ---------- Identificación del equipo ----------
        // La validación del código (12 dígitos) y sus mensajes de error están dentro de la pantalla.
        composable(Rutas.ESCANEAR) {
            ReportarEquipoScreen(
                onScan = { c, l, f, h ->
                    codigo = c.trim()
                    lab = l
                    fecha = f
                    hora = h
                    irA(Rutas.DETALLAR)
                }
            )
        }

        // ---------- Registro del daño ----------
        // La validación de tipos y descripción y sus mensajes de error están dentro de la pantalla.
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
                onAtras = { volver() },
                onContinuar = { irA(Rutas.CONFIRMAR) },
                modifier = Modifier.statusBarsPadding().navigationBarsPadding().imePadding()
            )
        }

        // ---------- Confirmación ----------
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
                onAtras = { volver() },
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
                        // Limpiamos todo SOLO al pulsar "Listo", para que el resumen no quede vacío detrás del cuadro
                        limpiarReporte()
                        // Según el flujo: Confirmación → Historial. Atrás en el historial vuelve al menú.
                        nav.navigate(Rutas.HISTORIAL) { popUpTo(Rutas.INICIO_USUARIO) }
                    }
                )
            }
        }

        // =====================================================
        //  INTEGRANTE 2: Historial de reportes + Detalle y seguimiento
        //  (reemplazar este bloque por sus pantallas)
        // =====================================================
        composable(Rutas.HISTORIAL) {
            PantallaPendiente(
                titulo = "Historial de reportes",
                responsable = "Integrante 2",
                onVolver = { volver() }
            )
        }

        // =====================================================
        //  INTEGRANTE 3: Panel del encargado + Lista de reportes
        //  (reemplazar este bloque por sus pantallas)
        // =====================================================
        composable(Rutas.ENCARGADO) {
            PantallaPendiente(
                titulo = "Panel del encargado",
                responsable = "Integrante 3",
                onVolver = { volver() }
            )
        }

        // =====================================================
        //  INTEGRANTE 4: Gestión del reporte
        //  (agregar aquí su composable)
        // =====================================================
    }
}