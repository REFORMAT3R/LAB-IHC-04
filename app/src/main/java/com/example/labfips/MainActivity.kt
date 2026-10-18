package com.example.labfips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.labfips.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Si tu proyecto tiene su propio tema (ej. LabFipsTheme), úsalo aquí en vez de MaterialTheme
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}
