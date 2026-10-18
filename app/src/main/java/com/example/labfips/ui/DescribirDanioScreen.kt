package com.example.labfips.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DescribirDanioScreen(vm: ReporteViewModel, onBack: () -> Unit, onContinuar: () -> Unit) {
    var menuAbierto by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> vm.fotoUri = uri?.toString() }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Atrás") }
        Text("Describe el daño", style = MaterialTheme.typography.headlineSmall)
        Text("Equipo: ${vm.codigo} (${vm.equipo ?: "-"})")

        Box {
            OutlinedButton(onClick = { menuAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                Text(vm.tipoDanio ?: "Selecciona el tipo de daño *")
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                vm.tiposDanio.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo) },
                        onClick = { vm.tipoDanio = tipo; menuAbierto = false }
                    )
                }
            }
        }
        if (vm.tipoDanio == null) {
            Text("Selecciona un tipo de daño para continuar",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = vm.descripcion,
            onValueChange = { vm.descripcion = it.take(200) },
            label = { Text(if (vm.tipoDanio == "Otro") "Descripción *" else "Descripción (opcional)") },
            isError = vm.descripcionError != null,
            supportingText = { Text(vm.descripcionError ?: "${vm.descripcion.length}/200") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (vm.fotoUri == null) "Adjuntar foto (opcional)" else "✔ Foto seleccionada") }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onContinuar,
            enabled = vm.formularioValido,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Revisar reporte") }
    }
}
