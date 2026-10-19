package com.example.labfips.navigation

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labfips.ui.ConfirmarScreen
import com.example.labfips.ui.DescribirDanioScreen
import com.example.labfips.ui.ReportarEquipoScreen
import com.example.labfips.ui.TipoDanio
import java.time.LocalDate
import java.time.LocalTime

private object Rutas {
    const val ESCANEAR = "escanear"
    const val DETALLAR = "detallar"
    const val CONFIRMAR = "confirmar"
}

// ---------- Reglas de validación (Fase 5) ----------
private val FORMATO_CODIGO = Regex("^[A-Z]{2,4}-[A-Z]+-\\d{3}$") // ej: PC-SISTEMAS-409
private const val MIN_DESCRIPCION = 10

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current

    // ---- Datos del reporte, compartidos entre pantallas ----
    var codigo by rememberSaveable { mutableStateOf("") }
    var lab by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var hora by rememberSaveable { mutableStateOf<LocalTime?>(null) }
    var tipo by rememberSaveable { mutableStateOf<TipoDanio?>(null) }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }

    fun aviso(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    NavHost(navController = nav, startDestination = Rutas.ESCANEAR) {

        // ---------- Pantalla 1: Escanear ----------
        composable(Rutas.ESCANEAR) {
            ReportarEquipoScreen(
                onScan = { c, l, f, h ->
                    val cod = c.trim().uppercase()
                    when {
                        cod.isBlank() ->
                            aviso("Ingresa o escanea el código del equipo")
                        !FORMATO_CODIGO.matches(cod) ->
                            aviso("Código no válido. Ejemplo: PC-SISTEMAS-409")
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
                tipoSeleccionado = tipo,
                descripcion = descripcion,
                fotoUri = fotoUri,
                onTipoChange = { tipo = it },
                onDescripcionChange = { descripcion = it },
                onFotoChange = { fotoUri = it },
                onAtras = { nav.popBackStack() },
                onContinuar = {
                    val desc = descripcion.trim()
                    val esOtro = tipo?.etiqueta.equals("Otro", ignoreCase = true)
                    when {
                        tipo == null ->
                            aviso("Selecciona el tipo de daño")
                        esOtro && desc.isEmpty() ->
                            aviso("Describe el daño para el tipo \"Otro\"")
                        desc.isNotEmpty() && desc.length < MIN_DESCRIPCION ->
                            aviso("La descripción debe tener al menos $MIN_DESCRIPCION caracteres")
                        else -> nav.navigate(Rutas.CONFIRMAR)
                    }
                },
                modifier = Modifier.statusBarsPadding().navigationBarsPadding()
            )
        }

        // ---------- Pantalla 3: Confirmar ----------
        composable(Rutas.CONFIRMAR) {
            ConfirmarScreen(
                codigoEquipo = codigo,
                ubicacion = lab,
                tipoSeleccionado = tipo,
                descripcion = descripcion,
                fotoUri = fotoUri,
                onAtras = { nav.popBackStack() },
                onEnviar = { mostrarConfirmacion = true },
                modifier = Modifier.statusBarsPadding().navigationBarsPadding()
            )

            if (mostrarConfirmacion) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacion = false },
                    title = { Text("¿Enviar reporte?") },
                    text = { Text("Una vez enviado no podrás modificarlo.") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacion = false
                            aviso("Reporte enviado exitosamente")
                            codigo = ""; lab = ""; fecha = null; hora = null
                            tipo = null; descripcion = ""; fotoUri = null
                            nav.navigate(Rutas.ESCANEAR) {
                                popUpTo(Rutas.ESCANEAR) { inclusive = true }
                            }
                        }) { Text("Enviar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacion = false }) { Text("Revisar") }
                    }
                )
            }
        }
    }
}
