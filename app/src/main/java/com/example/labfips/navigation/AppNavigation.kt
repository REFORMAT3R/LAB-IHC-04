package com.example.labfips.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.labfips.ui.DescribirDanioScreen
import com.example.labfips.ui.ReportarEquipoScreen
import com.example.labfips.ui.TipoDanio
import java.time.LocalDate
import java.time.LocalTime

private object Rutas {
    const val ESCANEAR = "escanear"
    const val DETALLAR = "detallar"
    // const val CONFIRMAR = "confirmar"   // pantalla 3 (pendiente)
}

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

    NavHost(navController = nav, startDestination = Rutas.ESCANEAR) {

        // ---------- Pantalla 1: Escanear ----------
        composable(Rutas.ESCANEAR) {
            ReportarEquipoScreen(
                onScan = { c, l, f, h ->
                    if (c.isBlank()) {
                        Toast.makeText(context, "Ingresa o escanea el código del equipo", Toast.LENGTH_SHORT).show()
                    } else {
                        codigo = c.trim()
                        lab = l
                        fecha = f
                        hora = h
                        nav.navigate(Rutas.DETALLAR)
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
                    // TODO: cuando exista la pantalla 3 -> nav.navigate(Rutas.CONFIRMAR)
                    Toast.makeText(context, "Pantalla 3 pendiente", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.statusBarsPadding().navigationBarsPadding()
            )
        }
    }
}