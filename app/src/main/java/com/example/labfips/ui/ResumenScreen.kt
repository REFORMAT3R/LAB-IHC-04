package com.example.labfips.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResumenScreen(vm: ReporteViewModel, onBack: () -> Unit, onEditar: () -> Unit, onFinalizar: () -> Unit) {
    var enviado by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!enviado) TextButton(onClick = onBack) { Text("← Atrás") }
        Text("Revisa tu reporte", style = MaterialTheme.typography.headlineSmall)
        Text("Equipo: ${vm.codigo} (${vm.equipo ?: "-"})")
        Text("Tipo de daño: ${vm.tipoDanio}")
        Text("Descripción: ${vm.descripcion.ifBlank { "Sin descripción" }}")
        Text("Foto: ${if (vm.fotoUri != null) "Adjunta" else "No adjuntada"}")

        if (enviado) {
            Text("✔ Reporte enviado correctamente", color = MaterialTheme.colorScheme.primary)
            Button(onClick = { vm.limpiar(); onFinalizar() }, modifier = Modifier.fillMaxWidth()) {
                Text("Nuevo reporte")
            }
        } else {
            Button(onClick = { enviado = true }, modifier = Modifier.fillMaxWidth()) { Text("Confirmar") }
            OutlinedButton(onClick = onEditar, modifier = Modifier.fillMaxWidth()) { Text("Editar") }
        }
    }
}
