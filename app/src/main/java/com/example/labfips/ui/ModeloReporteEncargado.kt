package com.example.labfips.ui

import androidx.compose.runtime.mutableStateListOf

/**
 * Modelo de un reporte visto por el encargado (Panel, Lista y Gestión).
 */
data class ReporteEncargado(
    val id: String,
    val ticket: String,
    val codigoEquipo: String,
    val laboratorio: String = "Lab. Computación IV",
    val fecha: String,
    val descripcion: String,
    val estado: String,
    /** Dirección de la foto guardada dentro de la app (null si no adjuntó foto). */
    val fotoUri: String? = null
)

/**
 * Reportes del encargado.
 * Es una lista "observable" (mutableStateListOf): cuando alguien la modifica
 * (por ejemplo la Gestión del reporte), las pantallas que la leen se actualizan solas.
 *
 * Los reportes 1, 2 y 3 son los MISMOS que ve el estudiante en reportesEstudianteMock
 * (mismo id, ticket, equipo y estado). Si cambias uno, cámbialo también en el otro archivo.
 */
val reportesMock = mutableStateListOf(
    ReporteEncargado(
        id = "1",
        ticket = "#TI-4402",
        codigoEquipo = "202411030001",
        laboratorio = "Lab. Computación IV",
        fecha = "15/11/2026",
        descripcion = "Las teclas enter y espacio no responden al presionar.",
        estado = "En revisión"
    ),
    ReporteEncargado(
        id = "2",
        ticket = "#TI-3810",
        codigoEquipo = "202411030014",
        laboratorio = "Lab. Redes",
        fecha = "12/11/2026",
        descripcion = "El monitor parpadea constantemente y se apaga.",
        estado = "Resuelto"
    ),
    ReporteEncargado(
        id = "3",
        ticket = "#TI-2104",
        codigoEquipo = "202411030027",
        laboratorio = "Lab. Computación III",
        fecha = "10/11/2026",
        descripcion = "Sin acceso a la red local del laboratorio.",
        estado = "Pendiente"
    ),
    ReporteEncargado(
        id = "4",
        ticket = "#TI-1830",
        codigoEquipo = "202411030008",
        laboratorio = "Lab. Computación IV",
        fecha = "10/11/2026",
        descripcion = "Teclado con varias teclas que no responden.",
        estado = "Resuelto"
    )
)