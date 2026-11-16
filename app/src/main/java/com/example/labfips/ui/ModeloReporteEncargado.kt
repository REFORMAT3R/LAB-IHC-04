package com.example.labfips.ui

/**
 * Modelo mock de un reporte visto por el encargado.
 * No sustituye el flujo de creación del estudiante: es solo para Panel y Lista.
 */
data class ReporteEncargado(
    val id: String,
    val ticket: String,
    val codigoEquipo: String,
    val laboratorio: String = "Lab. Computación IV",
    val fecha: String,
    val descripcion: String,
    val estado: String
)

/** Lista de prueba para visualizar el Panel y la Lista de reportes. */
val reportesMock = listOf(
    ReporteEncargado(
        id = "1",
        ticket = "#TI-1842",
        codigoEquipo = "202411030001",
        laboratorio = "Lab. Computación IV",
        fecha = "15/11/2026",
        descripcion = "La PC no enciende después de un corte de energía.",
        estado = "Pendiente"
    ),
    ReporteEncargado(
        id = "2",
        ticket = "#TI-1843",
        codigoEquipo = "202411030014",
        laboratorio = "Lab. Redes",
        fecha = "14/11/2026",
        descripcion = "El monitor parpadea y se pone en negro.",
        estado = "En revisión"
    ),
    ReporteEncargado(
        id = "3",
        ticket = "#TI-1844",
        codigoEquipo = "202411030027",
        laboratorio = "Lab. Computación III",
        fecha = "13/11/2026",
        descripcion = "No hay conexión a internet en el equipo.",
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
