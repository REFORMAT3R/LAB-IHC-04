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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
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
import com.example.labfips.ui.GestionReporteScreen
import com.example.labfips.ui.HistorialScreen
import com.example.labfips.ui.InicioScreen
import com.example.labfips.ui.InicioUsuarioScreen
import com.example.labfips.ui.ListaReportesScreen
import com.example.labfips.ui.ReportarEquipoScreen
import com.example.labfips.ui.ReporteEncargado
import com.example.labfips.ui.ReporteEnviadoDialog
import com.example.labfips.ui.ReporteEstudiante
import com.example.labfips.ui.TipoDanio
import com.example.labfips.ui.guardarFotoReporte
import com.example.labfips.ui.reportesEstudianteMock
import com.example.labfips.ui.reportesMock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

    // Integrante 4
    const val GESTION = "gestion/{id}"
}

/** Genera un ticket que no exista todavía en ninguna de las dos listas. */
private fun generarTicket(): String {
    var nuevo: String
    do {
        nuevo = "#TI-${(1000..9999).random()}"
    } while (
        reportesMock.any { it.ticket == nuevo } ||
        reportesEstudianteMock.any { it.ticket == nuevo }
    )
    return nuevo
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    // enviando: mientras se copia la foto (evita enviar dos veces)
    // fotoEnviada: si la foto realmente se guardó con el reporte
    var enviando by rememberSaveable { mutableStateOf(false) }
    var fotoEnviada by rememberSaveable { mutableStateOf(false) }

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
        enviando = false
        fotoEnviada = false
    }

    /**
     * Guarda el reporte recién enviado en las dos listas compartidas:
     * aparece en el Historial del estudiante y en la Lista del encargado, como "Pendiente".
     * [rutaFoto] es la copia de la foto guardada dentro de la app (o null si no hay foto).
     */
    fun registrarReporte(id: String, nuevoTicket: String, rutaFoto: String?) {
        val fechaTexto = (fecha ?: LocalDate.now()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val horaTexto = (hora ?: LocalTime.now()).format(DateTimeFormatter.ofPattern("HH:mm"))
        val tiposTexto = tipos.joinToString(", ") { it.etiqueta }
        val descripcionTexto = descripcion.trim().ifEmpty { "Sin descripción adicional." }

        reportesEstudianteMock.add(
            0,
            ReporteEstudiante(
                id = id,
                ticket = nuevoTicket,
                codigoEquipo = codigo,
                laboratorio = lab,
                fecha = fechaTexto,
                hora = horaTexto,
                tipoDanio = tiposTexto,
                descripcion = descripcionTexto,
                estado = "Pendiente",
                pasoActual = 1,
                fotoUri = rutaFoto
            )
        )
        reportesMock.add(
            0,
            ReporteEncargado(
                id = id,
                ticket = nuevoTicket,
                codigoEquipo = codigo,
                laboratorio = lab,
                fecha = fechaTexto,
                descripcion = descripcionTexto,
                estado = "Pendiente",
                fotoUri = rutaFoto
            )
        )
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
                // Si ya se está enviando, se ignoran los toques repetidos
                onEnviar = { if (!enviando) mostrarConfirmacion = true },
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
                            enviando = true

                            val nuevoTicket = generarTicket()
                            val idReporte = System.currentTimeMillis().toString()
                            val origen = fotoUri
                            ticket = nuevoTicket

                            scope.launch {
                                // 1) Copiar la foto dentro de la app (en segundo plano)
                                val rutaFoto = if (origen != null) {
                                    withContext(Dispatchers.IO) {
                                        guardarFotoReporte(context, origen, idReporte)
                                    }
                                } else null

                                // 2) Guardar el reporte para el Historial y para el encargado
                                registrarReporte(idReporte, nuevoTicket, rutaFoto)

                                fotoEnviada = rutaFoto != null
                                enviando = false
                                mostrarExito = true
                            }
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
                    conFoto = fotoEnviada,
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
                onAtras = { volver() },
                onReporteClick = { id -> irA("gestion/$id") }
            )
        }

        // INTEGRANTE 4
        composable(
            route = Rutas.GESTION,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            GestionReporteScreen(
                reporteId = id,
                onAtras = { volver() },
                onGuardado = { volver() }
            )
        }
    }
}