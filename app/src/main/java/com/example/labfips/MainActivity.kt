package com.example.labfips

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labfips.ui.DescribirDanioScreen
import com.example.labfips.ui.EscanearScreen
import com.example.labfips.ui.ReporteViewModel
import com.example.labfips.ui.ResumenScreen
import com.example.labfips.ui.theme.LabFIPSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabFIPSTheme {
                Surface {
                    val nav = rememberNavController()
                    val vm: ReporteViewModel = viewModel()

                    NavHost(nav, startDestination = "codigo", modifier = Modifier.safeDrawingPadding()) {
                        composable("codigo") {
                            EscanearScreen(vm) { nav.navigate("describir") }
                        }
                        composable("describir") {
                            DescribirDanioScreen(
                                vm,
                                onBack = { nav.popBackStack() },
                                onContinuar = { nav.navigate("resumen") }
                            )
                        }
                        composable("resumen") {
                            ResumenScreen(
                                vm,
                                onBack = { nav.popBackStack() },
                                onEditar = { nav.popBackStack() },
                                onFinalizar = { nav.popBackStack("codigo", false) }
                            )
                        }
                    }
                }
            }
        }
    }
}
