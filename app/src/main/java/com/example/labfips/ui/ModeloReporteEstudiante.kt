package com.example.labfips.ui

/**
 * Modelo de reporte para el flujo del estudiante (Historial y Detalle).
 */
data class ReporteEstudiante(
    val id: String,
    val ticket: String,
    val codigoEquipo: String,
    val laboratorio: String,
    val fecha: String,
    val hora: String,
    val tipoDanio: String,
    val descripcion: String,
    val estado: String,
    val pasoActual: Int,
    val observacionesEncargado: String? = null
)

val reportesEstudianteMock = listOf(
    ReporteEstudiante(
        id = "1",
        ticket = "#TI-4402",
        codigoEquipo = "202411030001",
        laboratorio = "Lab. Computación IV",
        fecha = "15/11/2026",
        hora = "10:30",
        tipoDanio = "Hardware / Teclado",
        descripcion = "Las teclas enter y espacio no responden al presionar.",
        estado = "En revisión",
        pasoActual = 2,
        observacionesEncargado = "Asignado al técnico de turno para cambio de periférico."
    ),
    ReporteEstudiante(
        id = "2",
        ticket = "#TI-3810",
        codigoEquipo = "202411030014",
        laboratorio = "Lab. Redes",
        fecha = "12/11/2026",
        hora = "14:15",
        tipoDanio = "Pantalla / Monitor",
        descripcion = "El monitor parpadea constantemente y se apaga.",
        estado = "Resuelto",
        pasoActual = 3,
        observacionesEncargado = "Cable HDMI defectuoso reemplazado con éxito."
    ),
    ReporteEstudiante(
        id = "3",
        ticket = "#TI-2104",
        codigoEquipo = "202411030027",
        laboratorio = "Lab. Computación III",
        fecha = "10/11/2026",
        hora = "09:00",
        tipoDanio = "Software / Red",
        descripcion = "Sin acceso a la red local del laboratorio.",
        estado = "Pendiente",
        pasoActual = 1,
        observacionesEncargado = null
    )
)
