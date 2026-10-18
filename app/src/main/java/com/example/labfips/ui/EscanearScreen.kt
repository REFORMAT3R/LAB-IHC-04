package com.example.labfips.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EscanearScreen(vm: ReporteViewModel, onContinuar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("LabFIPS", style = MaterialTheme.typography.headlineMedium)
        Text("Escanea o ingresa el código del equipo")

        OutlinedTextField(
            value = vm.codigo,
            onValueChange = { vm.codigo = it.uppercase() },
            label = { Text("Código del equipo") },
            isError = vm.codigoError != null,
            supportingText = { Text(vm.codigoError ?: "Ejemplo: LAB-PC-001") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (vm.codigoValido) {
            Text("Equipo: ${vm.equipo}", color = MaterialTheme.colorScheme.primary)
        }

        Button(
            onClick = onContinuar,
            enabled = vm.codigoValido,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continuar") }
    }
}
