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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.labfips.ui.ConfirmarScreen
import com.example.labfips.ui.DescribirDanioScreen
import com.example.labfips.ui.DetalleSeguimientoScreen
import com.example.labfips.ui.EncargadoDashboardScreen
import com.example.labfips.ui.HistorialScreen
import com.example.labfips.ui.InicioScreen
import com.example.labfips.ui.InicioUsuarioScreen
import com.example.labfips.ui.ListaReportesScreen
import com.example.labfips.ui.ReportarEquipoScreen
import com.example.labfips.ui.ReporteEnviadoDialog
import com.example.labfips.ui.TipoDanio
import java.time.LocalDate
import java.time.LocalTime

private object Rutas {
    const val INICIO = "inicio"
    const val INICIO_USUARIO = "inicio_usuario"
    const val ESCANEAR = "escanear"
    const val DETALLAR = "detallar"
    const val CONFIRMAR = "confirmar"

    // Integrante 2
    const val HISTORIAL = "historial"
    const val DETALLE = "detalle/{id}"

    // Integrante 3
    const val ENCARGADO = "encargado"
    const val LISTA_REPORTES = "lista_reportes"
}

private fun generarTicket() = "#TI-${(1000..9999).random()}"

@Composable
fun AppNavigation() {
    val nav = rememberNavController()

    var codigo by rememberSaveable { mutableStateOf("") }
    var lab by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var hora by rememberSaveable { mutableStateOf<LocalTime?>(null) }
    var tipos by rememberSaveable { mutableStateOf<Set<TipoDanio>>(emptySet()) }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    var mostrarExito by rememberSaveable { mutableStateOf(false) }
    var ticket by rememberSaveable { mutableStateOf("") }

    fun limpiarReporte() {
        codigo = ""
        lab = ""
        fecha = null
        hora = null
        tipos = emptySet()
        descripcion = ""
        fotoUri = null
        ticket = ""
        mostrarConfirmacion = false
        mostrarExito = false
    }

    fun puedeNavegar() =
        nav.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

    fun irA(ruta: String) {
        if (puedeNavegar()) nav.navigate(ruta)
    }

    fun volver() {
        if (puedeNavegar()) nav.popBackStack()
    }

    NavHost(navController = nav, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            InicioScreen(
                onEstudiante = { irA(Rutas.INICIO_USUARIO) },
                onEncargado = { irA(Rutas.ENCARGADO) }
            )
        }

        composable(Rutas.INICIO_USUARIO) {
            InicioUsuarioScreen(
                onNuevoReporte = {
                    limpiarReporte()
                    irA(Rutas.ESCANEAR)
                },
                onMisReportes = { irA(Rutas.HISTORIAL) },
                onSalir = {
                    if (puedeNavegar()) {
                        nav.navigate(Rutas.INICIO) {
                            popUpTo(Rutas.INICIO) { inclusive = true }
                        }
                    }
                }
            )
        }

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

        composable(Rutas.DETALLAR) {
            DescribirDanioScreen(
                codigoEquipo = codigo,
                tiposSeleccionados = tipos,
                descripcion = descripcion,
                fotoUri = fotoUri,
                onTipoToggle = { t -> tipos = if (t in tipos) tipos - t else tipos + t },
                onDescripcionChange = { descripcion = it },
                onFotoChange = { fotoUri = it },
                onAtras = { volver() },
                onContinuar = { irA(Rutas.CONFIRMAR) },
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            )
        }

        composable(Rutas.CONFIRMAR) {
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

            if (mostrarConfirmacion) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacion = false },
                    title = { Text("¿Enviar reporte?") },
                    text = { Text("Una vez enviado no podrás modificarlo.") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacion = false
                            ticket = generarTicket()
                            mostrarExito = true
                        }) { Text("Enviar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacion = false }) { Text("Revisar") }
                    }
                )
            }

            if (mostrarExito) {
                ReporteEnviadoDialog(
                    ticket = ticket,
                    conFoto = fotoUri != null,
                    onListo = {
                        limpiarReporte()
                        nav.navigate(Rutas.HISTORIAL) { popUpTo(Rutas.INICIO_USUARIO) }
                    }
                )
            }
        }

        // INTEGRANTE 2
        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                onAtras = { volver() },
                onReporteClick = { id -> irA("detalle/$id") }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "1"
            DetalleSeguimientoScreen(
                reporteId = id,
                onAtras = { volver() }
            )
        }

        // INTEGRANTE 3
        composable(Rutas.ENCARGADO) {
            EncargadoDashboardScreen(
                onVerReportes = { irA(Rutas.LISTA_REPORTES) },
                onVolver = { volver() }
            )
        }

        composable(Rutas.LISTA_REPORTES) {
            ListaReportesScreen(
                onAtras = { volver() }
            )
        }
    }
}
