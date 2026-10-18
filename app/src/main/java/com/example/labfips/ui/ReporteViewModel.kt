package com.example.labfips.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ReporteViewModel : ViewModel() {
    var codigo by mutableStateOf("")
    var tipoDanio by mutableStateOf<String?>(null)
    var descripcion by mutableStateOf("")
    var fotoUri by mutableStateOf<String?>(null)

    val tiposDanio = listOf("PC no enciende", "Monitor", "Teclado", "Mouse", "Otro")

    private val inventario = mapOf(
        "LAB-PC-001" to "PC 01 - Laboratorio 1",
        "LAB-PC-002" to "PC 02 - Laboratorio 1",
        "LAB-PC-003" to "PC 03 - Laboratorio 2"
    )

    val equipo: String? get() = inventario[codigo.trim()]

    val codigoError: String?
        get() = when {
            codigo.isBlank() -> null
            !Regex("^LAB-PC-\\d{3}$").matches(codigo.trim()) ->
                "Código no válido. Ejemplo: LAB-PC-001"
            codigo.trim() !in inventario -> "Equipo no encontrado"
            else -> null
        }

    val codigoValido: Boolean get() = codigo.isNotBlank() && codigoError == null

    val descripcionError: String?
        get() = when {
            tipoDanio == "Otro" && descripcion.isBlank() -> "Describe el daño"
            descripcion.isNotBlank() && descripcion.trim().length < 10 ->
                "Mínimo 10 caracteres"
            else -> null
        }

    val formularioValido: Boolean
        get() = tipoDanio != null && descripcionError == null

    fun limpiar() {
        codigo = ""; tipoDanio = null; descripcion = ""; fotoUri = null
    }
}
