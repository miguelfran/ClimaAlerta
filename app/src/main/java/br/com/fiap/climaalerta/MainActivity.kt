package br.com.fiap.climaalerta

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.fiap.climaalerta.ui.navigation.ClimaAlertaApp
import br.com.fiap.climaalerta.ui.theme.ClimaAlertaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            ClimaAlertaTheme {
                ClimaAlertaApp()
            }
        }
    }
}
